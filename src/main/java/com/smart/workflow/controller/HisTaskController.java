package com.smart.workflow.controller;

import com.smart.workflow.vo.PageVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 历史任务
 *
 * @author violet
 * @version 1.0
 * @date 2021/01/20 14:13
 */
@RestController
@RequestMapping("hisTask")
@Api(tags = "历史任务")
@Slf4j
public class HisTaskController {

    @Autowired
    private HistoryService historyService;

    @GetMapping("list")
    @ApiOperation("历史任务列表")
    public PageVo hisTaskList() {

        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().list();
        return new PageVo(list);
    }

    @GetMapping
    @ApiOperation("历史任务信息")
    public PageVo task(String processInstanceId) {
        return new  PageVo(historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list());
    }

}
