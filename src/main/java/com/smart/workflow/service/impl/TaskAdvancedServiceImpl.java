package com.smart.workflow.service.impl;

import com.smart.workflow.mapper.HisActivityDao;
import com.smart.workflow.po.HisActivity;
import com.smart.workflow.service.TaskAdvancedService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.bpmn.model.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author kurisu makise
 * @version 1.0
 * @date 2020/12/14 11:25
 */
@Service
@Slf4j
public class TaskAdvancedServiceImpl implements TaskAdvancedService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskRuntime taskRuntime;
    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private HisActivityDao hisActivityDao;

    @Autowired
    private FlowElementRelation flowElementRelation;

    @Override
    public void revoke(String businessKey) throws Exception {
        Task task = taskService.createTaskQuery().processInstanceBusinessKey(businessKey).singleResult();
        if (task == null) {
            throw new Exception("流程未启动或已执行完成，无法撤回");
        }

        List<HistoricTaskInstance> htiList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceBusinessKey(businessKey)
                .orderByTaskCreateTime()
                .asc()
                .list();
        String myTaskId = null;
        HistoricTaskInstance myTask = null;
        for (HistoricTaskInstance hti : htiList) {
            if (Authentication.getAuthenticatedUserId().equals(hti.getAssignee())) {
                myTaskId = hti.getId();
                myTask = hti;
                break;
            }
        }
        if (null == myTaskId) {
            throw new Exception("该任务非当前用户提交，无法撤回");
        }

        String processDefinitionId = myTask.getProcessDefinitionId();
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        //变量
//		Map<String, VariableInstance> variables = runtimeService.getVariableInstances(currentTask.getExecutionId());
        String myActivityId = null;
        List<HistoricActivityInstance> haiList = historyService.createHistoricActivityInstanceQuery()
                .executionId(myTask.getExecutionId()).finished().list();
        for (HistoricActivityInstance hai : haiList) {
            if (myTaskId.equals(hai.getTaskId())) {
                myActivityId = hai.getActivityId();
                break;
            }
        }
        FlowNode myFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(myActivityId);


        Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        String activityId = execution.getActivityId();
        log.warn("------->> activityId:" + activityId);
        FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(activityId);

        //记录原活动方向
        List<SequenceFlow> oriSequenceFlows = new ArrayList<SequenceFlow>();
        oriSequenceFlows.addAll(flowNode.getOutgoingFlows());

        //清理活动方向
        flowNode.getOutgoingFlows().clear();
        //建立新方向
        List<SequenceFlow> newSequenceFlowList = new ArrayList<SequenceFlow>();
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId");
//        newSequenceFlow.setSourceFlowElement(flowNode);
        newSequenceFlow.setTargetFlowElement(myFlowNode);
        newSequenceFlowList.add(newSequenceFlow);
        flowNode.setOutgoingFlows(newSequenceFlowList);

        taskService.addComment(task.getId(), task.getProcessInstanceId(), "撤回");


        Map<String, Object> currentVariables = new HashMap<>(1);
        currentVariables.put("applier", "loginUser.getUsername()");
        //完成任务
        taskService.complete(task.getId(), currentVariables);
        //恢复原方向
        flowNode.setOutgoingFlows(oriSequenceFlows);
    }

    @Override
    public void jump(String sourceTaskId, String targetTaskId) {
        Task sourceTask = taskService.createTaskQuery().taskId(sourceTaskId).singleResult();
        HisActivity targetActivity = hisActivityDao.selectByTaskId(targetTaskId);

        //查询所有在途任务
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(sourceTask.getProcessInstanceId()).list();

        //查询bpmn定义
        BpmnModel bpmnModel = repositoryService.getBpmnModel(sourceTask.getProcessDefinitionId());
        Set<String> childElement = flowElementRelation.getChildElementName(bpmnModel, targetActivity.getActId());
        //原节点
        FlowNode sourceNode = (FlowNode) bpmnModel.getFlowElement(sourceTask.getTaskDefinitionKey());
        //目标节点
        FlowNode targetNode = (FlowNode) bpmnModel.getFlowElement(targetActivity.getActId());

        //只有一个任务直接退回
        if (tasks.size() == 1) {
            jumpToTarget(sourceTaskId, sourceNode, targetNode);

        } else if (tasks.size() > 1) {
            //如果存在多个在途任务，回退和当前任务同级的其他任务
            ParallelGateway parallelGateway = flowElementRelation.getParallelGateway(bpmnModel);

            //网关指向目标节点，sourceNode指向网关 ，网关输入修改为当前任务列表，提交任务
            if (parallelGateway != null) {
                //记录并行网关原目标
                List<SequenceFlow> outgoingFlows = parallelGateway.getOutgoingFlows();
                List<SequenceFlow> incomingFlows = parallelGateway.getIncomingFlows();
                //设置网关流出节点
                changeOutgoing(parallelGateway, targetNode);

                //查询所有需要跳转到网关的节点
                List<FlowNode> sourceFlowNodeList = new ArrayList<>();
                for (Task task : tasks) {
                    if (childElement.contains(task.getTaskDefinitionKey())) {
                        sourceFlowNodeList.add((FlowNode) bpmnModel.getFlowElement(task.getTaskDefinitionKey()));
                    }
                }
                //设置网关流入节点
                changeIncoming(sourceFlowNodeList, parallelGateway);

                //提交跳转
                for (Task task : tasks) {
                    if (childElement.contains(task.getTaskDefinitionKey())) {
                        jumpToTarget(task.getId(), (FlowNode) bpmnModel.getFlowElement(task.getTaskDefinitionKey()), parallelGateway);
                    }
                }
                //还原网关
                parallelGateway.setOutgoingFlows(outgoingFlows);
                parallelGateway.setIncomingFlows(incomingFlows);
            }
        }
    }

    /**
     * 节点跳转
     *
     * @param sourceTaskId 原任务id
     * @param sourceNode   节点自身
     * @param targetNode   跳转目标
     */
    private void jumpToTarget(String sourceTaskId, FlowNode sourceNode, FlowNode targetNode) {
        //记录原跳转方向
        List<SequenceFlow> sourceOutgoingFlows = sourceNode.getOutgoingFlows();
        //设置新跳转方向
        changeOutgoing(sourceNode, targetNode);
        //提交任务
        taskService.complete(sourceTaskId);
        //还原 源跳转方向
        sourceNode.setOutgoingFlows(sourceOutgoingFlows);
    }

    /**
     * 改变节点流转目标
     *
     * @param sourceNode 节点自身
     * @param targetNode 跳转到哪
     */
    private void changeOutgoing(FlowNode sourceNode, FlowNode targetNode) {
        changeOutgoing(sourceNode, Collections.singletonList(targetNode));
    }

    /**
     * 改变节点流转目标
     *
     * @param sourceNode     节点自身
     * @param targetNodeList 跳转到哪
     */
    private void changeOutgoing(FlowNode sourceNode, List<FlowNode> targetNodeList) {
        List<SequenceFlow> sequenceFlowList = new ArrayList<>(targetNodeList.size());
        for (FlowElement targetFlowNode : targetNodeList) {
            SequenceFlow sequenceFlow = new SequenceFlow();
            sequenceFlow.setId(UUID.randomUUID().toString());
            sequenceFlow.setSourceFlowElement(sourceNode);
            sequenceFlow.setTargetFlowElement(targetFlowNode);
            sequenceFlowList.add(sequenceFlow);
        }
        sourceNode.setOutgoingFlows(sequenceFlowList);
    }

    /**
     * 设置节点自来哪
     *
     * @param sourceNode 来自哪个节点
     * @param targetNode 节点自身
     */
    private void changeIncoming(FlowNode sourceNode, FlowNode targetNode) {
        changeIncoming(Collections.singletonList(sourceNode), targetNode);
    }

    /**
     * 设置节点自来哪
     *
     * @param sourceNodeList 来自哪个节点
     * @param targetNode     节点自身
     */
    private void changeIncoming(List<FlowNode> sourceNodeList, FlowNode targetNode) {
        List<SequenceFlow> sequenceFlowList = new ArrayList<>(sourceNodeList.size());
        for (FlowNode sourceFlowNode : sourceNodeList) {
            SequenceFlow sequenceFlow = new SequenceFlow();
            sequenceFlow.setId(UUID.randomUUID().toString());
            sequenceFlow.setSourceFlowElement(sourceFlowNode);
            sequenceFlow.setTargetFlowElement(targetNode);
            sequenceFlowList.add(sequenceFlow);
        }
        targetNode.setIncomingFlows(sequenceFlowList);
    }


    @Override
    public void transfer(String taskId, String replaceUser) {
        taskService.setAssignee(taskId, replaceUser);
    }

    @Override
    public void finish(String businessKey) {
    }
}
