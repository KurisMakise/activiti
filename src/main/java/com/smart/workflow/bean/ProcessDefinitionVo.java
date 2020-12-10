package com.smart.workflow.bean;

import lombok.Data;

/**
 * @author kurisu makise
 * @version 1.0
 * @date 2020/9/30 15:13
 */
@Data
public class ProcessDefinitionVo {
    private String id;

    private String category;

    private String name;

    private String key;

    private String description;

    private int version;

    private String resourceName;

    private String deploymentId;

    private String diagramResourceName;

    private boolean hasStartFormKey;

    private boolean hasGraphicalNotation;

    private boolean isSuspended;

    private String tenantId;

    private String engineVersion;

    private Integer AppVersion;
}
