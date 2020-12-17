package com.smart.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiSort;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 流程图
 *
 * @author kurisu makise
 * @version 1.0
 * @date 2020/9/28 15:10
 */
@RestController
@Api(tags = "流程图")
@ApiSort(value = 5)
@RequestMapping("chart")

public class FlowChartController {


    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private RuntimeService runtimeService;

    private ProcessDiagramGenerator processDiagramGenerator = new DefaultProcessDiagramGenerator();


    @GetMapping("process/{processInstanceId}")
    public void image(HttpServletResponse response, @PathVariable String processInstanceId) throws IOException {
        InputStream svgStream = getDiagram(processInstanceId);

        PNGTranscoder transcoder = new PNGTranscoder();
        TranscoderInput input = new TranscoderInput(svgStream);
        TranscoderOutput output = new TranscoderOutput(response.getOutputStream());
        try {
            transcoder.transcode(input, output);
        } catch (TranscoderException e) {
            e.printStackTrace();
        }

    }

    public InputStream getDiagram(String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());

        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).unfinished().list();
        List<String> collect = list.stream().map(HistoricActivityInstance::getActivityId).collect(Collectors.toList());

        return processDiagramGenerator.generateDiagram(bpmnModel, collect, new ArrayList<>(0), "宋体", "宋体", "宋体");
    }

}

