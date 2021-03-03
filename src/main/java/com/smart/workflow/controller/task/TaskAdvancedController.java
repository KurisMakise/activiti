package com.smart.workflow.controller.task;

import com.smart.workflow.service.TaskAdvancedService;
import com.smart.workflow.vo.OptionVo;
import com.smart.workflow.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
    @PostMapping("{taskId}/revoke")
    @ApiOperation("任务撤销")
    public ResultVo revoke(@PathVariable String taskId) {
        try {
            taskAdvancedService.revoke(taskId);
            return new ResultVo();
        } catch (Exception e) {
            return new ResultVo(e.getMessage());
        }
    }


    @GetMapping("{taskId}/childNode")
    @ApiOperation("查询子节点列表")
    public Collection<OptionVo> getChildList(@PathVariable String taskId) {
        return taskAdvancedService.getChildNode(taskId);
    }


    @GetMapping("{taskId}/parentNode")
    @ApiOperation("查询父节点列表")
    public Collection<OptionVo> getParentList(@PathVariable String taskId) {
        return taskAdvancedService.getParentNode(taskId);
    }


    /**
     * 任意退回
     *
     * @param sourceTaskId 源任务id
     * @param targetActId  目标节点id
     */
    @PostMapping("jumpBackward")
    @ApiOperation("任意退回")
    public void jumpBackward(String sourceTaskId, String targetActId) {
        taskAdvancedService.jumpBackward(sourceTaskId, targetActId);
    }

    /**
     * 任意跳转
     *
     * @param sourceTaskId 源任务id
     * @param targetActId  节点定义id，查询跳转目标的流程定义
     */
    @PostMapping("jumpForward")
    @ApiOperation("任意提交")
    public void jumpForward(String sourceTaskId, String targetActId) {
        taskAdvancedService.jumpForward(sourceTaskId, targetActId);
    }


}
