package com.smart.workflow.vo;

import lombok.Data;

import java.util.List;

/**
 * 数据分页vo
 *
 * @author violet
 * @version 1.0
 * @date 2021/01/15 16:51
 */
@Data
public class PageVo {
    public PageVo() {

    }

    public PageVo(List<?> data) {
        this.data = data;
        success = true;
    }


    public PageVo(List<?> data, long total) {
        setData(data, total);
    }

    public PageVo setData(List<?> data, long total) {
        this.data = data;
        this.total = total;
        success = true;
        return this;
    }

    public int getStart() {
        return (current - 1) * pageSize;
    }

    public int getEnd() {
        return current * pageSize;
    }

    /**
     * 当前页
     */
    private int current = 1;

    /**
     * 返回数据
     */
    private List<?> data;

    /**
     * 分页大小
     */
    private int pageSize = 20;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 数据总数
     */
    private long total;
}
