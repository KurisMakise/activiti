package com.smart.workflow.service;

import com.smart.workflow.bean.ProcessDefinitionVo;
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
        ProcessInstance test = processController.start(processDefinitionVos.get(1).getId(), "test");
        try {

            Task task = taskController.getTaskByProcessInstanceId(test.getId());
            taskAdvancedService.replace(task.getId(), "yga");
        } catch (Exception e) {
            e.printStackTrace();
        }

        processController.delete(test.getId());
    }

    @Test
    void finish() {
    }
}