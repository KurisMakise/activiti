package com.smart.workflow.connector;

import org.activiti.api.process.model.IntegrationContext;
import org.activiti.api.process.runtime.connector.Connector;
import org.activiti.api.runtime.model.impl.IntegrationContextImpl;
import org.springframework.stereotype.Component;

/**
 * @author kurisu makise
 * @version 1.0
 * @date 2020/12/10 18:02
 */
@Component
public class TagTextConnector implements Connector {
    @Override
    public IntegrationContext apply(IntegrationContext integrationContext) {
        System.out.println("拒绝休假！！！！！！！！！！！！！！！！");

        IntegrationContextImpl integrationContext1 = new IntegrationContextImpl();
        integrationContext1.addInBoundVariable("approved",true);
        integrationContext1.addOutBoundVariable("approved",true);
        return integrationContext1;
    }
}
