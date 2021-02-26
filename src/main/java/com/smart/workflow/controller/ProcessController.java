package com.smart.workflow.controller;

import com.smart.workflow.vo.PageVo;
import com.smart.workflow.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 流程
 *
 * @author kurisu makise
 * @version 1.0
 * @date 2020/9/28 14:53
 */
@RestController
@RequestMapping("process")
@Api(tags = "实例控制")
@ApiSort(value = 3)
public class ProcessController {

    @Autowired
    private ProcessRuntime processRuntime;


    @GetMapping("instance")
    @ApiOperation("流程实例分页")
    public PageVo instance(PageVo pageVo) {
        try {
            Page<ProcessInstance> pageResult = processRuntime.processInstances(pageVo.getPageable());
            return pageVo.setData(pageResult.getContent(), pageResult.getTotalItems());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @GetMapping("{processDefinitionId}")
    @ApiOperation("流程实例")
    public ProcessDefinition process(@PathVariable String processDefinitionId) {
        return processRuntime.processDefinition(processDefinitionId);
    }

    @PostMapping("start")
    @ApiOperation("启动流程")
    public ResultVo start(String processDefinitionKey, String name, String businessKey, @RequestBody Map<String, Object> variables) {
        if (processDefinitionKey == null) {
            return new ResultVo("请选择流程!");
        }
        ProcessInstance start;
        try {
            start = processRuntime.start(ProcessPayloadBuilder.
                    start()
                    .withProcessDefinitionKey(processDefinitionKey)
                    .withName(name)
                    .withBusinessKey(businessKey)
                    .withVariables(variables)
                    .build());
        } catch (Exception e) {
            return new ResultVo(e.getMessage());
        }

        return new ResultVo(start);
    }

    @DeleteMapping("{processInstanceId}")
    @ApiOperation("删除流程实例")
    public void delete(@PathVariable String processInstanceId) {
        Arrays.stream(processInstanceId.split(",")).forEach(id -> {
            processRuntime.delete(ProcessPayloadBuilder
                    .delete(id));
        });

    }

    @PostMapping("{processInstanceId}/suspend")
    @ApiOperation("暂停实例")
    public void suspend(@PathVariable String processInstanceId) {
        processRuntime.suspend(ProcessPayloadBuilder.suspend(processInstanceId));
    }

    @PostMapping("{processInstanceId}/resume")
    @ApiOperation("恢复实例")
    public void resume(@PathVariable String processInstanceId) {
        processRuntime.resume(ProcessPayloadBuilder.resume(processInstanceId));
    }

}
