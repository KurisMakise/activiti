package com.smart.workflow.controller;

import com.smart.workflow.config.security.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.runtime.Execution;
import org.activiti.runtime.api.model.impl.APITaskConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务
 *
 * @author kurisu makise
 * @version 1.0
 * @date 2020/9/28 14:53
 */
@RestController
@RequestMapping("task")
@Slf4j
@Api(tags = "任务控制")
public class TaskController {

    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    /**
     * 流程撤销
     *
     * @throws ServiceException
     */
    @PostMapping("revoke")
    @ResponseBody
    public void revoke(String businessKey) throws ServiceException {
        org.activiti.engine.task.Task task = taskService.createTaskQuery().processInstanceBusinessKey(businessKey).singleResult();
        task = taskService.createTaskQuery().processInstanceId(businessKey).singleResult();
        if (task == null) {
            throw new ServiceException("流程未启动或已执行完成，无法撤回");
        }

        List<HistoricTaskInstance> htiList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(businessKey)
//                .processInstanceBusinessKey("businessKey")
                .orderByTaskCreateTime()
                .asc()
                .list();
        String myTaskId = null;
        HistoricTaskInstance myTask = null;
        for (HistoricTaskInstance hti : htiList) {
            if ("system".equals(hti.getAssignee())) {
                myTaskId = hti.getId();
                myTask = hti;
                break;
            }
        }
        if (null == myTaskId) {
            throw new ServiceException("该任务非当前用户提交，无法撤回");
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
        newSequenceFlow.setSourceFlowElement(flowNode);
        newSequenceFlow.setTargetFlowElement(myFlowNode);
        newSequenceFlowList.add(newSequenceFlow);
        flowNode.setOutgoingFlows(newSequenceFlowList);

        Authentication.setAuthenticatedUserId("loginUser.getUsername()");
        taskService.addComment(task.getId(), task.getProcessInstanceId(), "撤回");

        Map<String, Object> currentVariables = new HashMap<>(1);
        currentVariables.put("applier", "loginUser.getUsername()");
        //完成任务
        taskService.complete(task.getId(), currentVariables);
        //恢复原方向
        flowNode.setOutgoingFlows(oriSequenceFlows);
    }

    @Autowired
    private SecurityUtil securityUtil;

    @PostMapping("create")
    @ApiOperation("创建任务")
    public void create(String taskName, String description, String group, int priority) {
        securityUtil.logInAs("system");
        taskRuntime.create(
                TaskPayloadBuilder.create()
                        .withName(taskName)
                        .withDescription(description)
//                        .withCandidateGroup(group)
                        .withPriority(priority)
                        .build()
        );
    }

    @Autowired
    private APITaskConverter apiTaskConverter;

    @GetMapping("all")
    @ApiOperation("查询所有任务")
    public List<Task> taskAll() {
        return apiTaskConverter.from(taskService.createTaskQuery().list());
    }

    @GetMapping
    @ApiOperation("查询任务")
    public Page<Task> tasks(int start, int end) {
        securityUtil.logInAs("system");
        return taskRuntime.tasks(Pageable.of(start, end));
    }

    @GetMapping("{taskId}")
    @ApiOperation("查询单个任务")
    public Task task(@PathVariable String taskId) {
        return taskRuntime.task(taskId);
    }

    @PutMapping("{taskId}claim")
    @ApiOperation("认领任务")
    public void claim(@PathVariable String taskId) {
        log.info(">>> task:" + taskId + " complete");
        securityUtil.logInAs("system");
        taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(taskId).build());
    }

    @PutMapping("{taskId}complete")
    @ApiOperation(value = "完成任务", notes = "进入下一个节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "任务ID", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "variables", value = "填充参数", dataType = "body", paramType = "query"),
    })
    public void complete(@PathVariable String taskId) {
        log.info(">>> task:" + taskId + " complete");
        securityUtil.logInAs("system");
        taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(taskId).build());
    }

}
