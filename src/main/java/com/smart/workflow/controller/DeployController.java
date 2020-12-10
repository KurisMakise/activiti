package com.smart.workflow.controller;

import com.smart.workflow.bean.DeploymentVo;
import com.smart.workflow.bean.ProcessDefinitionVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kurisu makise
 * @version 1.0
 * @date 2020/12/10 16:46
 */


@RequestMapping("deploy")
@RestController
@Api(tags = "流程部署")
@ApiSort(value = 2)
@Slf4j
public class DeployController {

    @Autowired
    private RepositoryService repositoryService;


    @GetMapping("{deploymentId}")
    @ApiOperation("流程部署详情")
    public DeploymentVo deploymentDetail(@PathVariable String deploymentId) {
        Deployment deployment = repositoryService.createDeploymentQuery().
                deploymentId(deploymentId).singleResult();
        DeploymentVo deploymentVo = new DeploymentVo();
        BeanUtils.copyProperties(deployment, deploymentVo);
        return deploymentVo;
    }


    @GetMapping
    @ApiOperation("部署列表")
    public List<DeploymentVo> deployList() {
        List<Deployment> list = repositoryService.createDeploymentQuery().list();

        return list.stream().map((entity) -> {
            DeploymentVo deploymentVo = new DeploymentVo();
            BeanUtils.copyProperties(entity, deploymentVo);
            return deploymentVo;
        }).collect(Collectors.toList());
    }


    @DeleteMapping("{deploymentId}")
    @ApiOperation("删除已发布实例")
    public void delete(@PathVariable String deploymentId) {
        repositoryService.deleteDeployment(deploymentId);
    }



    @GetMapping("definition")
    @ApiOperation("已部署流程定义")
    public List<ProcessDefinitionVo> definitionList() {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();

        return list.stream().map((entity) -> {
            ProcessDefinitionVo processDefinitionVo = new ProcessDefinitionVo();
            BeanUtils.copyProperties(entity, processDefinitionVo);
            return processDefinitionVo;
        }).collect(Collectors.toList());
    }
}
