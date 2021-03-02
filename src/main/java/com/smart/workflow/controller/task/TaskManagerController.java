package com.smart.workflow.controller.task;

import com.smart.workflow.utils.StringUtils;
import com.smart.workflow.vo.PageVo;
import com.smart.workflow.vo.TaskVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.runtime.api.model.impl.APITaskConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务管理
 *
 * @author violet
 * @version 1.0
 * @date 2021/3/1 13:41
 */
@RequestMapping("task/manager")
@RestController
@Slf4j
@Api(tags = "任务管理")
@PreAuthorize("hasAnyRole('ACTIVITI_MANAGER','ADMIN')")
public class TaskManagerController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private APITaskConverter apiTaskConverter;


    @Autowired
    private RepositoryService repositoryService;

    @GetMapping("list")
    @ApiOperation("分页查询")
    public PageVo list(PageVo pageVo) {
        List<Task> tasks = taskService.createTaskQuery().listPage(pageVo.getFirstResult(), pageVo.getPageSize());

        List<org.activiti.api.task.model.Task> data = apiTaskConverter.from(tasks);

        List<TaskVo> taskVos = data.stream().map(task -> {
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
        pageVo.setData(taskVos, taskService.createTaskQuery().count());
        return pageVo;
    }

}
