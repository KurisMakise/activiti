package com.smart.workflow.mapper;

import com.smart.workflow.po.HisActivity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HisActivityDao {

    HisActivity selectByPrimaryKey(String id);

    HisActivity selectByTaskId(String taskId);

    List<Object> origSelect(@Param("sql") String sql);
}