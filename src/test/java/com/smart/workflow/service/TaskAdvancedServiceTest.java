package com.smart.workflow.service;

import com.smart.workflow.config.context.SpringContextHolder;
import com.smart.workflow.config.security.SecurityUtil;
import com.smart.workflow.controller.DeployController;
import com.smart.workflow.controller.ModelController;
import com.smart.workflow.controller.ProcessController;
import com.smart.workflow.controller.TaskController;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.task.model.Task;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author kurisu makise
 * @version 1.0
 * @date 2020/12/14 14:33
 */
@RunWith(SpringRunner.class)
@SpringBootTest
class TaskAdvancedServiceTest {

    @Autowired
    private TaskAdvancedService taskAdvancedService;

    @Autowired
    private ProcessController processController;

    @Autowired
    private TaskController taskController;

    @Autowired
    private ModelController modelController;

    @Autowired
    private DeployController deployController;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private RepositoryService repositoryService;


    @Test
    void revoke() {
        try {
            taskAdvancedService.revoke("test");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void jump() {
        taskAdvancedService.jump("", "");
    }

    @Test
    void replace() {
        Model model = repositoryService.createModelQuery().modelName("审批流程").singleResult();

        Deployment processUnitTest = modelController.deploy(model.getId(), "processUnitTest");

        //启动流程
        String businessKey = UUID.randomUUID().toString();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(processUnitTest.getId()).singleResult();
        ProcessInstance test = processController.start(processDefinition.getId(), "test", businessKey, null);
        try {
            securityUtil.logInAs("supervisor");
            Task task = taskController.getTaskByProcessInstanceId(test.getId());
            taskController.claim(task.getId());
            Map<String, Object> variables = new HashMap<>(2);
            variables.put("approval", true);
            variables.put("day", 8);
            taskController.complete(task.getId(), variables);

            org.activiti.engine.task.Task task1 = SpringContextHolder.getBean(TaskService.class).createTaskQuery().taskCandidateGroup("manager").singleResult();
            securityUtil.logInAs("manager");
            taskController.claim(task1.getId());
            taskController.complete(task1.getId(), variables);

            task1 = SpringContextHolder.getBean(TaskService.class).createTaskQuery().taskCandidateGroup("generalManager").singleResult();
            securityUtil.logInAs("generalManager");
            taskController.claim(task1.getId());
            taskController.complete(task1.getId(), variables);

//            taskAdvancedService.transfer(task.getId(), "user");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //删除实例
        processController.delete(test.getId());
        //删除发布流程
        deployController.delete(processUnitTest.getId());
    }


    @Test
    void finish() {
    }
}