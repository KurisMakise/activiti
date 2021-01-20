package com.smart.workflow.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 前端下拉框对象
 *
 * @author violet
 * @version 1.0
 * @date 2021/01/19 14:45
 */
@Data
public class OptionVo {

    /**
     * 选项值
     */
    @JSONField(name = "id")
    private String value;

    /**
     * 页面显示名称
     */
    @JSONField(name = "name")
    private String label;
}
