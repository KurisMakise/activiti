package com.smart.workflow.controller;

import com.alibaba.fastjson.JSON;
import com.smart.workflow.config.context.SpringContextHolder;
import com.smart.workflow.config.security.SecurityUtil;
import com.smart.workflow.service.TaskAdvancedService;
import com.smart.workflow.vo.gantt.Data;
import com.smart.workflow.vo.gantt.GanttVo;
import com.smart.workflow.vo.gantt.Link;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.task.model.Task;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author kurisu makise
 * @version 1.0
 * @date 2020/12/16 16:04
 */
@RestController
@RequestMapping("test")
@Api(tags = "流程测试")
public class TestController {

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

    @ApiOperation("甘特图数据")
    @GetMapping("gantt")
    public Object ganttData() {
        List<Data> data = new ArrayList<>();
        List<Link> links = new ArrayList<>();

        data.add(new Data("1", "任务1", new Date(), null, null, 0d, true));
        data.add(new Data("2", "任务1-2", new Date(), 2, "1", 0.3, false));
        data.add(new Data("3", "任务1-3", new Date(), 5, "1", 0.5, false));
        data.add(new Data("4", "任务2", new Date(), null, null, 1d, true));
        data.add(new Data("5", "任务2-1", new Date(), 5, "4", 0.8, false));
        data.add(new Data("6", "任务2-3", new Date(), 10, "4", 0d, false));
        return JSON.toJSONString(new GanttVo(data, links));
    }


    @ApiOperation("测试流程")
    @GetMapping
    public void testFlow() {
        Model model = repositoryService.createModelQuery().modelName("审批流程").singleResult();

        Deployment processUnitTest = (Deployment) modelController.deploy(model.getId(), "processUnitTest").getData();

        //启动流程
        String businessKey = UUID.randomUUID().toString();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(processUnitTest.getId()).singleResult();
        ProcessInstance test = (ProcessInstance) processController.start(processDefinition.getId(), "test", businessKey, null).getData();
        try {
            securityUtil.logInAs("supervisor");
            Task task = taskController.getTaskByProcessInstanceId(test.getId()).get(0);
            String targetId = task.getId();

            taskController.claim(task.getId());
            Map<String, Object> variables = new HashMap<>(2);
            variables.put("approval", true);
            variables.put("day", 8);
            taskController.complete(task.getId(), variables);

            TaskService taskService = SpringContextHolder.getBean(TaskService.class);
            List<org.activiti.engine.task.Task> list = taskService.createTaskQuery().processInstanceBusinessKey(businessKey).list();
            for (org.activiti.engine.task.Task task1 : list) {
                taskService.complete(task1.getId());
            }

            task = taskController.getTaskByProcessInstanceId(test.getId()).get(0);
            taskAdvancedService.jumpBackward(task.getId(), targetId);

            list = taskService.createTaskQuery().processInstanceBusinessKey(businessKey).list();
            for (org.activiti.engine.task.Task task1 : list) {
                taskService.complete(task1.getId());
            }
            list = taskService.createTaskQuery().processInstanceBusinessKey(businessKey).list();
            for (org.activiti.engine.task.Task task1 : list) {
                taskService.complete(task1.getId());
            }


            org.activiti.engine.task.Task task1 = SpringContextHolder.getBean(TaskService.class).createTaskQuery().taskCandidateGroup("manager").singleResult();
            securityUtil.logInAs("manager");
            taskController.claim(task1.getId());
            taskController.complete(task1.getId(), variables);


            task1 = SpringContextHolder.getBean(TaskService.class).createTaskQuery().taskCandidateGroup("manager").singleResult();
            securityUtil.logInAs("manager");
            taskController.claim(task1.getId());
            taskController.complete(task1.getId(), variables);

            task1 = SpringContextHolder.getBean(TaskService.class).createTaskQuery().taskCandidateGroup("generalManager").singleResult();
            securityUtil.logInAs("generalManager");
            taskController.claim(task1.getId());
            taskController.complete(task1.getId(), variables);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //删除实例
        processController.delete(test.getId());
        //删除发布流程
        deployController.delete(processUnitTest.getId());
    }

}
