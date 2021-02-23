package com.smart.workflow.vo.gantt;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author violet
 * @version 1.0
 * @date 2021/2/22 17:41
 */
@lombok.Data
public class Data {
    private String id;
    private String text;
    @JSONField(format = "yyyy/MM/dd")
    private Date start_date;
    private Integer duration;
    private String parent;
    private Double progress;
    private Boolean open;

    public Data(String id, String text, Date start_date, Integer duration, String parent, Double progress, Boolean open) {
        this.id = id;
        this.text = text;
        this.start_date = start_date;
        this.duration = duration;
        this.parent = parent;
        this.progress = progress;
        this.open = open;
    }
}
