package com.smart.workflow.service;

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
     * @param businessKey 业务id
     * @param target      跳转目标
     */
    void jump(String businessKey, String target);

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


}
