package com.smart.workflow.config.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * @author kurisu makise
 * @version 1.0
 * @date 2020/12/16 14:09
 * <p>
 * <p>
 * Reports problems related to nullability annotations: overriding problems (for example, when a nullable parameter is annotated as not-null in the overriding method), non-annotated getters of annotated fields, and so on.
 */
@Component
@NotNull
public class SpringContextHolder implements ApplicationContextAware {
    private static ApplicationContext context;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static Object getBean(String name) {
        return context.getBean(name);
    }
}
