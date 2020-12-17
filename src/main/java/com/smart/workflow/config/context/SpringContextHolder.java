package com.smart.workflow.config.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author kurisu makise
 * @version 1.0
 * @date 2020/12/16 14:09
 */
@Component
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
