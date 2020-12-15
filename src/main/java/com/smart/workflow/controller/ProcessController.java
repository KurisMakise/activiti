package com.smart.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 流程
 *
 * @author kurisu makise
 * @version 1.0
 * @date 2020/9/28 14:53
 */
@RestController
@RequestMapping("process")
@Api(tags = "模型实例控制")
@ApiSort(value = 3)
public class ProcessController {

    @Autowired
    private ProcessRuntime processRuntime;

    @GetMapping
    @ApiOperation("流程定义分页")
    public Page<ProcessDefinition> processes(int start, int end) {
        return processRuntime.processDefinitions(Pageable.of(start, end));
    }

    @GetMapping("instance")
    @ApiOperation("流程实例分页")
    public Page<ProcessInstance> instance(int start, int end) {
        return processRuntime.processInstances(Pageable.of(start, end));
    }

    @GetMapping("{processDefinitionId}")
    @ApiOperation("流程实例")
    public ProcessDefinition process(@PathVariable String processDefinitionId) {
        return processRuntime.processDefinition(processDefinitionId);
    }

    @PutMapping("start")
    @ApiOperation("启动流程")
    public ProcessInstance start(String processDefinitionKey, String name) {
        return processRuntime.start(ProcessPayloadBuilder.
                start()
                .withProcessDefinitionKey(processDefinitionKey)
                .withName(name)
                .withVariables(null)
                .build());

    }

    @PutMapping("{processInstanceId}/delete")
    @ApiOperation("删除流程实例")
    public void delete(@PathVariable String processInstanceId) {
        processRuntime.delete(ProcessPayloadBuilder
                .delete(processInstanceId));
    }

}
