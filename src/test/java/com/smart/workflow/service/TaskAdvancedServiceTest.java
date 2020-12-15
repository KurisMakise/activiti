package com.smart.workflow.service;

import com.smart.workflow.bean.ProcessDefinitionVo;
import com.smart.workflow.config.security.SecurityUtil;
import com.smart.workflow.controller.DeployController;
import com.smart.workflow.controller.ProcessController;
import com.smart.workflow.controller.TaskController;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.task.model.Task;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
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
    private DeployController deployController;

    @Autowired
    private SecurityUtil securityUtil;

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
        //查询流程定义
        List<ProcessDefinitionVo> processDefinitionVos = deployController.definitionList();
        //启动流程
        String businessKey = UUID.randomUUID().toString();
        ProcessInstance test = processController.start(processDefinitionVos.get(1).getId(), "test", businessKey, null);

        try {
            securityUtil.logInAs("admin");
            Task task = taskController.getTaskByProcessInstanceId(test.getId());

            taskController.claim(task.getId());
            securityUtil.logInAs("user");
            taskAdvancedService.transfer(task.getId(), "user");
            taskController.complete(task.getId(), null);
            taskAdvancedService.revoke(businessKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        processController.delete(test.getId());
    }


    @Test
    void finish() {
    }
}