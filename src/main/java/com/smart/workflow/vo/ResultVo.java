package com.smart.workflow.vo;

import lombok.Data;

/**
 * 结果
 *
 * @author violet
 * @version 1.0
 * @date 2021/01/25 11:25
 */
@Data
public class ResultVo {

    private boolean success;

    private String message;

    private Object data;

    public ResultVo(String message) {
        this.message = message;
        this.success = false;
    }

    public ResultVo(Object data) {
        this.data = data;
        this.success = true;
    }

    public ResultVo() {
        this.success = true;
    }
}
