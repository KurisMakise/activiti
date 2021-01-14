package com.smart.workflow.controller;

import com.smart.workflow.service.TaskAdvancedService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.FlowNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

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
    private TaskAdvancedService taskAdvancedService;

    /**
     * 流程撤销
     */
    @PostMapping("revoke")
    @ApiOperation("任务拿回")
    @ResponseBody
    public void revoke(String businessKey) throws Exception {

    }

    @GetMapping("{taskId}/childNode")
    @ApiOperation("查询子节点列表")
    @ResponseBody
    public Collection<FlowNode> getChildList(@PathVariable String taskId) {
        return taskAdvancedService.getChildNode(taskId);
    }


    @GetMapping("{taskId}/parentNode")
    @ApiOperation("查询父节点列表")
    @ResponseBody
    public Collection<FlowNode> getParentList(@PathVariable String taskId) {
        return taskAdvancedService.getParentNode(taskId);
    }


    /**
     * 任意退回
     *
     * @param sourceTaskId 源任务id
     * @param targetTaskId 历史任务id
     */
    @PostMapping("jumpBackward")
    @ApiOperation("任意退回")
    @ResponseBody
    public void jumpBackward(String sourceTaskId, String targetTaskId) {
        taskAdvancedService.jumpBackward(sourceTaskId, targetTaskId);
    }

    /**
     * 任意跳转
     *
     * @param sourceTaskId 源任务id
     * @param targetActId  节点定义id，查询跳转目标的流程定义
     */
    @PostMapping("jumpForward")
    @ApiOperation("任意提交")
    @ResponseBody
    public void jumpForward(String sourceTaskId, String targetActId) {
        taskAdvancedService.jumpForward(sourceTaskId, targetActId);
    }


}
