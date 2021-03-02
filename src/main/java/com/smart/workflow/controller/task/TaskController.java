package com.smart.workflow.controller.task;

import com.smart.workflow.config.security.SecurityUtil;
import com.smart.workflow.utils.StringUtils;
import com.smart.workflow.vo.PageVo;
import com.smart.workflow.vo.ResultVo;
import com.smart.workflow.vo.TaskVo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
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

    @Autowired
    private RepositoryService repositoryService;

    @GetMapping("list")
    @ApiOperation("查询所有任务")
    public PageVo taskAll(PageVo pageVo) {
        Page<Task> page = taskRuntime.tasks(pageVo.getPageable());
        List<TaskVo> data = page.getContent().stream().map(task -> {
            TaskVo taskVo = new TaskVo();
            BeanUtils.copyProperties(task, taskVo);
            taskVo.setVariables(taskService.getVariables(task.getId()));
            taskVo.setFormKey(StringUtils.convertStr(taskVo.getFormKey(), taskVo.getVariables()));
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(taskVo.getProcessDefinitionId()).singleResult();
            if (processDefinition != null) {
                taskVo.setProcessName(processDefinition.getName());
            }
            return taskVo;
        }).collect(Collectors.toList());

        return new PageVo(data, page.getTotalItems());
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
    public ResultVo claim(@PathVariable String taskId) {
        try {
            taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(taskId).build());
            return new ResultVo();
        } catch (Exception e) {
            return new ResultVo(e.getMessage());
        }
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
            if (variables.containsKey("comment")) {
                org.activiti.engine.task.Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
                taskService.addComment(taskId, task.getProcessInstanceId(), variables.get("comment") + "");
            }
            taskService.complete(taskId, variables, true);
        } catch (Exception e) {
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
    public ResultVo setAssignee(@PathVariable String taskId, String userId) {
        if (userId == null) {
            return new ResultVo("替换人不能为空");
        }
        try {
            taskService.setAssignee(taskId, userId);
            return new ResultVo();
        } catch (Exception e) {
            return new ResultVo(e.getMessage());
        }
    }
}
