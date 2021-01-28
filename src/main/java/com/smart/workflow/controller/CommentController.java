package com.smart.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 评论
 *
 * @author violet
 * @version 1.0
 * @date 2021/01/27 13:57
 */
@RestController
@RequestMapping("comment")
@Api(tags = "评论")
@Slf4j
public class CommentController {
    @Autowired
    private TaskService taskService;

    @GetMapping("task")
    @ApiOperation("任务评论")
    public List<Comment> getComments(String taskId) {
        return taskService.getTaskComments(taskId);
    }

    @GetMapping("process")
    @ApiOperation("流程评论")
    public List<Comment> getProcessComments(String processInstanceId) {
        return taskService.getProcessInstanceComments(processInstanceId);
    }

}
