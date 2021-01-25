package com.smart.workflow.vo;

import lombok.Data;

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
    private String id;
    private String name;
    private Integer priority;
    private String processDefinitionId;
    private String processInstanceId;
    private String standalone;
    private String status;
    private String taskDefinitionKey;
    private Long duration;
    private Map<String, Object> variables;
}
