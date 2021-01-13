package com.smart.workflow.service.impl;

import org.activiti.bpmn.model.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 流程元素关联关系
 *
 * @author violet
 * @version 1.0
 * @date 2021/01/11 16:57
 */
@Component
public class FlowElementRelation {

    /**
     * 获取流程图里的网关对象
     *
     * @param bpmnModel 流程定义
     * @return 网关
     */
    public ParallelGateway getParallelGateway(BpmnModel bpmnModel) {
        Collection<FlowElement> flowElementList = getFlowElementList(bpmnModel);
        for (FlowElement flowElement : flowElementList) {
            if (flowElement instanceof ParallelGateway) {
                return (ParallelGateway) flowElement;
            }
        }
        return null;
    }

    /**
     * 查询一个节点下的所有子节点
     *
     * @param bpmnModel 流程定义
     * @param elementId 节点id
     * @return 子节点名称
     */
    public Set<String> getChildElementName(BpmnModel bpmnModel, String elementId) {
        Set<String> flowElementIdSet = new HashSet<>();

        Map<String, FlowElement> flowElementMap = getFlowElementMap(bpmnModel);
        iterElement(flowElementIdSet, flowElementMap, elementId);

        return flowElementIdSet;
    }

    /**
     * 递归查询元素
     *
     * @param flowElementIdSet 所有子节点id集合
     * @param flowElementMap   流程定义集合
     * @param elementId        元素id
     */
    private void iterElement(Set<String> flowElementIdSet, Map<String, FlowElement> flowElementMap, String elementId) {
        FlowElement flowElement = flowElementMap.get(elementId);

        if (flowElement == null) {
            return;
        }

        if (flowElement instanceof FlowNode) {
            List<SequenceFlow> outgoingFlows = ((FlowNode) flowElement).getOutgoingFlows();
            for (SequenceFlow outFlow : outgoingFlows) {
                String outFlowElementId = outFlow.getTargetRef();
                //解决死循环
                if (flowElementIdSet.contains(outFlowElementId)) {
                    continue;
                }
                flowElementIdSet.add(outFlowElementId);
                iterElement(flowElementIdSet, flowElementMap, outFlowElementId);
            }
        }
    }

    private Map<String, FlowElement> getFlowElementMap(BpmnModel bpmnModel) {
        return bpmnModel.getProcesses().get(0).getFlowElementMap();
    }

    private Collection<FlowElement> getFlowElementList(BpmnModel bpmnModel) {
        return bpmnModel.getProcesses().get(0).getFlowElements();
    }
}
