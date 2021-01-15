package com.smart.workflow.vo;

import lombok.Data;

/**
 * 流程节点
 *
 * @author violet
 * @version 1.0
 * @date 2021/01/14 16:56
 */
@Data
public class FlowNodeVo {
    /**
     * actId
     */
    private String id;

    /**
     * 名称
     */
    private String name;
}
