package com.smart.workflow.mapper;

import com.smart.workflow.po.HisActivity;

public interface HisActivityDao {

    HisActivity selectByPrimaryKey(String id);

    HisActivity selectByTaskId(String taskId);

}