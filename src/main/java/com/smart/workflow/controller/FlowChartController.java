package com.smart.workflow.controller;

import io.swagger.annotations.Api;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 流程图
 *
 * @author kurisu makise
 * @version 1.0
 * @date 2020/9/28 15:10
 */
@RestController
@Api(tags = "流程图")
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

//        FileUtils.copyInputStreamToFile(svgStream, new File("D:444.svg"));

        TranscoderInput input = new TranscoderInput(svgStream);
        PNGTranscoder transcoder = new PNGTranscoder();
        TranscoderOutput output = new TranscoderOutput(response.getOutputStream());
        try {
            transcoder.transcode(input, output);
        } catch (TranscoderException e) {
            e.printStackTrace();
        }

    }

    public InputStream getDiagram(String processInstanceId) {
        ProcessInstance processInstance = processRuntime.processInstance(processInstanceId);
        String processDefinitionId;
        if (processInstance == null) {
            //查询已经结束的流程实例
            HistoricProcessInstance processInstanceHistory =
                    historyService.createHistoricProcessInstanceQuery()
                            .processInstanceId(processInstanceId).singleResult();
            if (processInstanceHistory == null) {
                return null;
            } else {
                processDefinitionId = processInstanceHistory.getProcessDefinitionId();
            }
        } else {
            processDefinitionId = processInstance.getProcessDefinitionId();
        }

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        List<String> activeActivityIds = runtimeService.getActiveActivityIds(processInstance.getId());
        return processDiagramGenerator.generateDiagram(bpmnModel, activeActivityIds, new ArrayList<String>(0), "宋体", "宋体", "宋体");
    }

}
