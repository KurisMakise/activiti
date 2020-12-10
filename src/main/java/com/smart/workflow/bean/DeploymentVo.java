package com.smart.workflow.bean;

import lombok.Data;

import java.util.Date;

/**
 * 部署流程
 *
 * @author kurisu makise
 * @version 1.0
 * @date 2020/9/30 10:09
 */
@Data
public class DeploymentVo {
    String id;

    String name;

    Date deploymentTime;

    String category;

    String key;

    String tenantId;

    Integer version;

    String projectReleaseVersion;

}
