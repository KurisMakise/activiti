package com.smart.workflow.po;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 */
@Data
public class HisActivity implements Serializable {
    private String id;

    private String procDefId;

    private String procInstId;

    private String executionId;

    private String actId;

    private String taskId;

    private String callProcInstId;

    private String actName;

    private String actType;

    private String assignee;

    private Date startTime;

    private Date endTime;

    private Long duration;

    private String deleteReason;

    private String tenantId;

    private static final long serialVersionUID = 1L;
}