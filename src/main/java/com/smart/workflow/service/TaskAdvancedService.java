package com.smart.workflow.service;

import com.smart.workflow.vo.FlowNodeVo;

import java.util.Collection;
import java.util.List;

/**
 * 任务特殊处理功能
 *
 * @author kurisu makise
 * @version 1.0
 * @date 2020/12/14 11:25
 */
public interface TaskAdvancedService {
    /**
     * 任务取回
     *
     * @param businessKey 业务id
     */
    void revoke(String businessKey) throws Exception;

    /**
     * 任意跳转
     *
     * @param sourceTaskId 源任务id
     * @param targetTaskId 历史任务id
     */
    void jumpBackward(String sourceTaskId, String targetTaskId);

    /**
     * 任意跳转
     *
     * @param sourceTaskId 源任务id
     * @param targetActId  节点定义id，查询跳转目标的流程定义
     */
    void jumpForward(String sourceTaskId, String targetActId);

    /**
     * 替换待办人员
     *
     * @param taskId      任务id
     * @param replaceUser 替换人员
     */
    void transfer(String taskId, String replaceUser);

    /**
     * 终止流程
     *
     * @param businessKey 业务id
     */
    void finish(String businessKey);


    /**
     * 查询任务子节点列表
     *
     * @param taskId 任务id
     * @return 子节点列表
     */
    Collection<FlowNodeVo> getChildNode(String taskId);


    /**
     * 查询任务父节点列表
     *
     * @param taskId 任务id
     * @return 子节点列表
     */
    Collection<FlowNodeVo> getParentNode(String taskId);
}
