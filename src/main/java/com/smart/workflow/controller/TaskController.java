package com.smart.workflow.controller;

import com.smart.workflow.config.security.SecurityUtil;
import com.smart.workflow.utils.StringUtils;
import com.smart.workflow.vo.PageVo;
import com.smart.workflow.vo.ResultVo;
import com.smart.workflow.vo.TaskVo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.TaskService;
import org.activiti.runtime.api.model.impl.APITaskConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
@ApiSort(value = 4)

public class TaskController {

    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private TaskService taskService;


    @Autowired
    private SecurityUtil securityUtil;

    @PostMapping("create")
    @ApiOperation("创建任务")
    public void create(String taskName, String description, String group, int priority) {
        taskRuntime.create(
                TaskPayloadBuilder.create()
                        .withName(taskName)
                        .withDescription(description)
//                        .withCandidateGroup(group)
                        .withPriority(priority)
                        .build()
        );
    }

    @GetMapping("processInstanceId")
    @ApiOperation("通过实例id查询任务")
    public List<Task> getTaskByProcessInstanceId(String processInstanceId) {
        return apiTaskConverter.from(taskService.createTaskQuery().processInstanceId(processInstanceId).list());

    }

    @GetMapping("businessKey")
    @ApiOperation("通过业务id查询任务")
    public Task getTaskByBusinessKey(String businessKey) {
        return apiTaskConverter.from(taskService.createTaskQuery().processInstanceBusinessKey(businessKey).singleResult());
    }

    @Autowired
    private APITaskConverter apiTaskConverter;

    @GetMapping("list")
    @ApiOperation("查询所有任务")
    public PageVo taskAll() {
        List<Task> taskList = apiTaskConverter.from(taskService.createTaskQuery().list());
        List<TaskVo> data = taskList.stream().map(task -> {
            TaskVo taskVo = new TaskVo();
            BeanUtils.copyProperties(task, taskVo);
            taskVo.setVariables(taskService.getVariables(task.getId()));
            taskVo.setFormKey(StringUtils.convertStr(taskVo.getFormKey(), taskVo.getVariables()));
            return taskVo;
        }).collect(Collectors.toList());
        return new PageVo(data, taskList.size());
    }

    @GetMapping
    @ApiOperation("查询任务")
    public Page<Task> tasks(int start, int end) {
        return taskRuntime.tasks(Pageable.of(start, end));
    }

    @GetMapping("{taskId}")
    public Task task(@PathVariable String taskId) {
        return taskRuntime.task(taskId);
    }

    @PostMapping("{taskId}/claim")
    @ApiOperation("认领任务")
    public void claim(@PathVariable String taskId) {
        taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(taskId).build());
    }


    @PostMapping("{taskId}/complete")
    @ApiOperation(value = "完成任务", notes = "进入下一个节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "任务ID", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "variables", value = "填充参数", dataType = "body", paramType = "query"),
    })
    public ResultVo complete(@PathVariable String taskId, @RequestBody Map<String, Object> variables) {
        log.info(">>> task:" + taskId + " complete");
        try {
            taskService.complete(taskId, variables);
        } catch (ActivitiException e) {
            return new ResultVo(e.getMessage());
        }
        return new ResultVo();
    }

    @GetMapping("{taskId}/params")
    @ApiOperation("任务参数")
    public Map<String, Object> params(@PathVariable String taskId) {
        return taskService.getVariables(taskId);
    }

    @PostMapping("{taskId}/assignee")
    @ApiOperation("设置任务处理人")
    public void setAssignee(@PathVariable String taskId, String userId) {
        taskService.setAssignee(taskId, userId);
    }

}
