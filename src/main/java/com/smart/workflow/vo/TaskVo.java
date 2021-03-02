package com.smart.workflow.vo;

import lombok.Data;
import org.activiti.api.task.model.Task;

import java.util.Date;
import java.util.Map;

/**
 * 任务显示
 *
 * @author violet
 * @version 1.0
 * @date 2021/01/21 17:51
 */
@Data
public class TaskVo {
    private Date reatedDate;
    private Date createdDate;
    private Date dueDate;
    private Date claimedDate;
    private String id;
    private String name;
    private Integer priority;
    private String assignee;
    private String description;
    private String processName;
    private String processDefinitionId;
    private String processInstanceId;
    private String standalone;
    private Task.TaskStatus status;
    private String taskDefinitionKey;
    private String formKey;
    private Long duration;
    private Map<String, Object> variables;
}
