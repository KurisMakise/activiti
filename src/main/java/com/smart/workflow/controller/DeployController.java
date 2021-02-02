package com.smart.workflow.controller;

import com.alibaba.fastjson.JSONObject;
import com.smart.workflow.bean.DeploymentVo;
import com.smart.workflow.bean.ProcessDefinitionVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.runtime.api.model.impl.APITaskConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author kurisu makise
 * @version 1.0
 * @date 2020/12/10 16:46
 */


@RequestMapping("deploy")
@RestController
@Api(tags = "模型部署")
@ApiSort(value = 2)
@Slf4j
public class DeployController {

    @Autowired
    private RepositoryService repositoryService;

    @GetMapping("{deploymentId}")
    public void test(@PathVariable String deploymentId) {
        Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
    }

    @DeleteMapping("{deploymentId}")
    @ApiOperation("删除已发布实例")
    public void delete(@PathVariable String deploymentId) {
        //删除发布实例   级联删除 repositoryService.deleteDeployment(deploymentId, true)
        Arrays.stream(deploymentId.split(",")).forEach(id -> {
            repositoryService.deleteDeployment(id);
        });
    }


    @GetMapping
    @ApiOperation("已部署流程")
    public List<DeploymentVo> deploy() {
        return repositoryService.createDeploymentQuery().list().stream().map(deployment -> {
            DeploymentVo deploymentVo = new DeploymentVo();
            BeanUtils.copyProperties(deployment, deploymentVo);
            return deploymentVo;
        }).collect(Collectors.toList());
    }

    @GetMapping("definition")
    @ApiOperation("已部署流程定义")
    public List<ProcessDefinitionVo> processDefinition() {
        return repositoryService.createProcessDefinitionQuery().list().stream().map(processDefinition -> {
            ProcessDefinitionVo processDefinitionVo = new ProcessDefinitionVo();
            BeanUtils.copyProperties(processDefinition, processDefinitionVo);
            return processDefinitionVo;
        }).collect(Collectors.toList());
    }
}
