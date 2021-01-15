package com.smart.workflow.service.impl;

import com.smart.workflow.mapper.HisActivityDao;
import com.smart.workflow.service.TaskAdvancedService;
import com.smart.workflow.vo.FlowNodeVo;
import lombok.extern.slf4j.Slf4j;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.bpmn.model.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    }

    private BpmnModel getBpmnByTask(String taskId) {
        Task sourceTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        return repositoryService.getBpmnModel(sourceTask.getProcessDefinitionId());
    }

    @Override
    public void jumpBackward(String sourceTaskId, String targetTaskId) {
        String targetActId = hisActivityDao.selectByTaskId(targetTaskId).getActId();
        jump(sourceTaskId, targetActId, flowElementRelation.getChildNode(getBpmnByTask(sourceTaskId), targetActId).keySet());
    }

    @Override
    public void jumpForward(String sourceTaskId, String targetActId) {
        jump(sourceTaskId, targetActId, flowElementRelation.getParentNode(getBpmnByTask(sourceTaskId), targetActId).keySet());
    }

    @Override
    public Collection<FlowNodeVo> getChildNode(String taskId) {
        return flowElementRelation.getChildNode(taskId).values()
                .stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public Collection<FlowNodeVo> getParentNode(String taskId) {
        return flowElementRelation.getParentNode(taskId).values().
                stream().map(this::convert).collect(Collectors.toList());
    }

    private FlowNodeVo convert(FlowNode flowNode) {
        FlowNodeVo flowNodeVo = new FlowNodeVo();
        BeanUtils.copyProperties(flowNode, flowNodeVo);
        return flowNodeVo;
    }

    /**
     * 任意跳转
     *
     * @param sourceTaskId 当前任务id
     * @param targetActId  目标
     * @param elementIdSet 跳转到目标节点所经过的节点集合，如果当前任务包含则需要跳转所有节点
     */

    private void jump(String sourceTaskId, String targetActId, Set<String> elementIdSet) {
        //查询任务
        Task sourceTask = taskService.createTaskQuery().taskId(sourceTaskId).singleResult();
        //查询bpmn定义
        BpmnModel bpmnModel = repositoryService.getBpmnModel(sourceTask.getProcessDefinitionId());

        jump(bpmnModel, sourceTask, targetActId, elementIdSet);
    }

    private void jump(BpmnModel bpmnModel, Task sourceTask, String targetActId, Set<String> elementIdSet) {
        //原节点
        FlowNode sourceNode = (FlowNode) bpmnModel.getFlowElement(sourceTask.getTaskDefinitionKey());
        //目标节点
        FlowNode targetNode = (FlowNode) bpmnModel.getFlowElement(targetActId);

        //查询所有在途任务
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(sourceTask.getProcessInstanceId()).list();
        //只有一个任务直接退回
        if (tasks.size() == 1) {
            jumpToTarget(sourceTask.getId(), sourceNode, targetNode);
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
                    if (elementIdSet.contains(task.getTaskDefinitionKey())) {
                        sourceFlowNodeList.add((FlowNode) bpmnModel.getFlowElement(task.getTaskDefinitionKey()));
                    }
                }
                //设置网关流入节点
                changeIncoming(sourceFlowNodeList, parallelGateway);

                //提交跳转
                for (Task task : tasks) {
                    if (elementIdSet.contains(task.getTaskDefinitionKey())) {
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
