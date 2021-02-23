package com.smart.workflow.vo.gantt;

import lombok.Data;

/**
 * @author violet
 * @version 1.0
 * @date 2021/2/22 17:41
 */
@Data
public class Link {
    private String id;
    private String source;
    private String target;
    private String type;
}
