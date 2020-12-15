package com.smart.workflow.controller;

import com.smart.workflow.config.security.SecurityUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.TaskService;
import org.activiti.runtime.api.model.impl.APITaskConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Task getTaskByProcessInstanceId(String processInstanceId) {
        return apiTaskConverter.from(taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult());

    }

    @GetMapping("businessKey")
    @ApiOperation("通过业务id查询任务")
    public Task getTaskByBusinessKey(String businessKey) {
        return apiTaskConverter.from(taskService.createTaskQuery().processInstanceBusinessKey(businessKey).singleResult());
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
        return taskRuntime.tasks(Pageable.of(start, end));
    }

    @GetMapping("{taskId}")
    public Task task(@PathVariable String taskId) {
        return taskRuntime.task(taskId);
    }

    @PutMapping("{taskId}/claim")
    @ApiOperation("认领任务")
    public void claim(@PathVariable String taskId) {
        taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(taskId).build());
    }


    @PutMapping("{taskId}/complete")
    @ApiOperation(value = "完成任务", notes = "进入下一个节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "任务ID", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "variables", value = "填充参数", dataType = "body", paramType = "query"),
    })
    public void complete(@PathVariable String taskId, Map<String, Object> variables) {
        log.info(">>> task:" + taskId + " complete");

        taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(taskId).withVariables(variables).build());
    }

}
