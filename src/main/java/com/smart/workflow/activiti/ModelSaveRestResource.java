package com.smart.workflow.activiti;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RestController
public class ModelSaveRestResource   {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ModelSaveRestResource.class);

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = {"/service/model/{modelId}/save"}, method = {org.springframework.web.bind.annotation.RequestMethod.PUT})
    @ResponseStatus(HttpStatus.OK)
    public void saveModel(@PathVariable String modelId,String name, String description,String json_xml,String svg_xml) {
        try {
            Model model = this.repositoryService.getModel(modelId);
            System.out.println("ModelSaveRestResource.saveModel----------");
            ObjectNode modelJson = (ObjectNode) this.objectMapper.readTree(model.getMetaInfo());
            modelJson.put("name", name);
            modelJson.put("description", description);
            model.setMetaInfo(modelJson.toString());
            model.setName(name);
            this.repositoryService.saveModel(model);
            this.repositoryService.addModelEditorSource(model.getId(), json_xml.getBytes(StandardCharsets.UTF_8));

            PNGTranscoder transcoder = new PNGTranscoder();
            TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(svg_xml.getBytes(StandardCharsets.UTF_8)));
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();

            transcoder.transcode(input, new TranscoderOutput(outStream));

            this.repositoryService.addModelEditorSourceExtra(model.getId(), outStream.toByteArray());
            outStream.close();
        } catch (Exception e) {
            LOGGER.error("Error saving model", e);
            throw new ActivitiException("Error saving model", e);
        }
    }
}
