package com.smart.workflow.controller.task;

import com.smart.workflow.vo.PageVo;
import com.smart.workflow.vo.TaskVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    public PageVo hisTaskList(PageVo pageVo) {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .orderByTaskCreateTime().desc().listPage(pageVo.getFirstResult(), pageVo.getPageSize());
        pageVo.setData(list, historyService.createHistoricTaskInstanceQuery().count());

        return pageVo;
    }

    @GetMapping
    @ApiOperation("历史任务信息")
    public PageVo task(String processInstanceId) {
        return new PageVo(historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list());
    }

    @GetMapping("done")
    @ApiOperation("我的已办")
    public PageVo done(PageVo pageVo) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new Exception("当前用户未登录!");
        }

        String userName = authentication.getName();
        HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
                .or().taskAssignee(userName).taskOwner(userName).endOr();
        List<HistoricTaskInstance> data =
                historicTaskInstanceQuery
                        .orderByHistoricTaskInstanceEndTime().desc()
                        .listPage(pageVo.getFirstResult(), pageVo.getPageSize());

        pageVo.setData(data, historicTaskInstanceQuery.count());
        return pageVo;
    }

}
