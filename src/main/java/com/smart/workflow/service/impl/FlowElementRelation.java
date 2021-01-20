package com.smart.workflow.service.impl;

import org.activiti.bpmn.model.*;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskService taskService;

    private enum QueryType {
        /**
         * 查询子节点
         */
        CHILD(0),
        /**
         * 查询父节点
         */
        PARENT(1);

        private final int type;

        QueryType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

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
     * 查询任务的所有子节点
     *
     * @param taskId 任务id
     * @return 子节点集合
     */

    public Map<String, FlowNode> getChildNode(String taskId) {
        Task sourceTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (sourceTask == null) {
            return new HashMap<>(0);
        }

        BpmnModel bpmnModel = repositoryService.getBpmnModel(sourceTask.getProcessDefinitionId());
        FlowElement flowElement = bpmnModel.getFlowElement(sourceTask.getTaskDefinitionKey());

        return getChildNode(bpmnModel, flowElement.getId());
    }

    /**
     * 查询任务所有父节点
     *
     * @param taskId 任务id
     * @return 子节点集合
     */

    public Map<String, FlowNode> getParentNode(String taskId) {
        Task sourceTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (sourceTask == null) {
            return new HashMap<>(0);
        }
        BpmnModel bpmnModel = repositoryService.getBpmnModel(sourceTask.getProcessDefinitionId());
        FlowElement flowElement = bpmnModel.getFlowElement(sourceTask.getTaskDefinitionKey());

        return getParentNode(bpmnModel, flowElement.getId());
    }


    /**
     * element节点的所有子节点
     *
     * @param bpmnModel 流程定义
     * @param elementId 节点id
     * @return 子节点id
     */
    public Map<String, FlowNode> getChildNode(BpmnModel bpmnModel, String elementId) {
        Map<String, FlowNode> flowNodeMap = new LinkedHashMap<>(20);

        iterElement(flowNodeMap, getFlowElementMap(bpmnModel), elementId, QueryType.CHILD);

        return flowNodeMap;
    }


    /**
     * element节点下的所有父节点
     *
     * @param bpmnModel 流程定义
     * @param elementId 节点id
     * @return 子节点id
     */
    public Map<String, FlowNode> getParentNode(BpmnModel bpmnModel, String elementId) {
        Map<String, FlowNode> flowNodeMap = new LinkedHashMap<>(20);

        iterElement(flowNodeMap, getFlowElementMap(bpmnModel), elementId, QueryType.PARENT);

        return flowNodeMap;
    }

    /**
     * 递归查询元素
     *
     * @param resultMap      返回结果
     * @param flowElementMap 流程定义集合
     * @param flowElementId  元素id
     * @param queryType      {@link QueryType}
     */
    private void iterElement(Map<String, FlowNode> resultMap, Map<String, FlowElement> flowElementMap, String flowElementId, QueryType queryType) {
        FlowElement flowElement = flowElementMap.get(flowElementId);

        if (flowElement == null) {
            return;
        }

        if (flowElement instanceof FlowNode) {
            List<SequenceFlow> sequenceFlowList;

            //判断查询父节点还是子节点
            if (queryType == QueryType.PARENT) {
                sequenceFlowList = ((FlowNode) flowElement).getIncomingFlows();
            } else {
                sequenceFlowList = ((FlowNode) flowElement).getOutgoingFlows();
            }

            for (SequenceFlow sequenceFlow : sequenceFlowList) {
                String tmpElementId;
                FlowElement tmpElement;

                //查询父节点，获取从哪来
                if (queryType == QueryType.PARENT) {
                    tmpElementId = sequenceFlow.getSourceRef();
                    tmpElement = sequenceFlow.getSourceFlowElement();
                } else {
                    tmpElementId = sequenceFlow.getTargetRef();
                    tmpElement = sequenceFlow.getTargetFlowElement();
                }
                //解决死循环
                if (resultMap.containsKey(tmpElementId)) {
                    continue;
                }
                //只保存用户操作节点
                if (tmpElement instanceof UserTask) {
                    resultMap.put(tmpElementId, (UserTask) tmpElement);
                }
                iterElement(resultMap, flowElementMap, tmpElementId, queryType);
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
