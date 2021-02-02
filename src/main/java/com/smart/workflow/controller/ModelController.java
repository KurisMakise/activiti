package com.smart.workflow.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 资源服务
 *
 * @author kurisu makise
 * @version 1.0
 * @date 2020/9/29 15:25
 */
@RequestMapping("model")
@RestController
@Api(tags = "模型管理")
@ApiSort(value = 1)
@Slf4j
public class ModelController {

    @Autowired
    private RepositoryService repositoryService;

    @GetMapping
    @ApiOperation("流程模型列表")
    public List<Model> list() {
        ModelQuery modelQuery = repositoryService.createModelQuery();
        return modelQuery.list();
    }

    @GetMapping("{modelId}")
    @ApiOperation("流程模型详情")
    public Model model(@PathVariable String modelId) {
        return repositoryService.getModel(modelId);
    }


    @DeleteMapping("{modelId}")
    @ApiOperation(("删除模型"))
    public Object delete(@PathVariable String modelId) {
        Arrays.stream(modelId.split(",")).forEach(item -> repositoryService.deleteModel(item));
        return null;
    }

    private void setImplementType(FlowElement flowElement) {
        if (flowElement instanceof ServiceTask) {
            ((ServiceTask) flowElement).setImplementationType(null);
        }
    }

    @PostMapping("{modelId}/deploy")
    @ApiOperation("流程发布")
    public Deployment deploy(@PathVariable String modelId, @RequestParam(defaultValue = "activiti工作流") String processName) {
        Deployment deployment = null;
        try {
            byte[] sourceBytes = repositoryService.getModelEditorSource(modelId);

            JsonNode editorNode = new ObjectMapper().readTree(sourceBytes);
            BpmnModel bpmnModel = (new BpmnJsonConverter()).convertToBpmnModel(editorNode);

            //去除implementationType,  任务事件 使用spring bean 注入对象
            bpmnModel.getProcesses().forEach(process -> process.getFlowElements().forEach(this::setImplementType));

            DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
                    .enableDuplicateFiltering()
                    .name(processName)
                    .addBpmnModel(processName.concat(".bpmn20.xml"), bpmnModel);
            deployment = deploymentBuilder.deploy();
        } catch (Exception e) {
            log.error("根据modelId部署流程,异常:{}", e.getMessage());
        }
        return deployment;
    }
}
