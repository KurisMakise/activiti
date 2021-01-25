package com.smart.workflow.service;

import com.smart.workflow.vo.OptionVo;

import java.util.Collection;

/**
 * 任务特殊处理功能
 *
 * @author kurisu makise
 * @version 1.0
 * @date 2020/12/14 11:25
 */
public interface TaskAdvancedService {
    /**
     * 任务撤销
     *
     * @param taskId 任务id
     */
    void revoke(String taskId);



    /**
     * 任意跳转
     *
     * @param sourceTaskId 源任务id
     * @param targetActId  目标节点id
     */
    void jumpBackward(String sourceTaskId, String targetActId);

    /**
     * 任意跳转
     *
     * @param sourceTaskId 源任务id
     * @param targetActId  节点定义id，查询跳转目标的流程定义
     */
    void jumpForward(String sourceTaskId, String targetActId);

    /**
     * 查询任务子节点列表
     *
     * @param taskId 任务id
     * @return 子节点列表
     */
    Collection<OptionVo> getChildNode(String taskId);


    /**
     * 查询任务父节点列表
     *
     * @param taskId 任务id
     * @return 子节点列表
     */
    Collection<OptionVo> getParentNode(String taskId);
}
