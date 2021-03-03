package com.smart.workflow.listener;

import lombok.extern.slf4j.Slf4j;
import org.activiti.api.task.runtime.events.*;
import org.activiti.api.task.runtime.events.listener.TaskRuntimeEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 监听配置
 *
 * @author kurisu makise
 * @version 1.0
 * @date 2020/9/28 16:39
 */
@Configuration
@Slf4j
public class TaskEventListener {


    @Bean
    public TaskRuntimeEventListener<TaskCreatedEvent> taskCreatedListener() {
        return taskCreatedEvent -> {

            log.info(">>> task created:'"
                    + taskCreatedEvent.getEntity().getName() + "'");
        };
    }

    @Bean
    public TaskRuntimeEventListener<TaskAssignedEvent> taskAssignedListener() {
        return taskAssignedEvent ->
                log.info(">>> task assigned:'"
                        + taskAssignedEvent.getEntity().getName() + "'");
    }

    @Bean
    public TaskRuntimeEventListener<TaskCompletedEvent> taskCompletedListener() {
        return taskCompletedEvent ->
                log.info(">>> task completed:'"
                        + taskCompletedEvent.getEntity().getName());
    }

    @Bean
    public TaskRuntimeEventListener<TaskActivatedEvent> taskActivatedListener() {
        return taskActivatedEvent ->
                log.info(">>> task activated:'"
                        + taskActivatedEvent.getEntity().getName());
    }

    @Bean
    public TaskRuntimeEventListener<TaskCancelledEvent> taskCancelledListener() {
        return taskCancelledEvent ->
                log.info(">>> task cancel:'" +
                        taskCancelledEvent.getEntity().getName());
    }
}
