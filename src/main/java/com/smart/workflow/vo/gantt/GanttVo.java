package com.smart.workflow.vo.gantt;

import java.util.List;

/**
 * @author violet
 * @version 1.0
 * @date 2021/2/22 17:40
 */
@lombok.Data
public class GanttVo {
    private List<Data> data;
    private List<Link> links;

    public GanttVo(List<Data> data, List<Link> links) {
        this.data = data;
        this.links = links;
    }
}
