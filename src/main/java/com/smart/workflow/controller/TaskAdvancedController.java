package com.smart.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 任务高级功能
 *
 * @author kurisu makise
 * @version 1.0
 * @date 2020/12/14 11:20
 */
@RestController
@RequestMapping("task/advanced")
@Slf4j
@Api(tags = "任务扩展控制")

public class TaskAdvancedController {

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
    @ApiOperation("任务拿回")
    @ResponseBody
    public void revoke(String businessKey) throws Exception {

    }

}
