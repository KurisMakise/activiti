package com.smart.workflow.connector;

import org.activiti.api.process.model.IntegrationContext;
import org.activiti.api.process.runtime.connector.Connector;
import org.activiti.api.runtime.model.impl.IntegrationContextImpl;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * @author kurisu makise
 * @version 1.0
 * @date 2020/12/10 17:41
 */
@Component
public class ProcessTextConnector  implements Connector {
    @Override
    public IntegrationContext apply(IntegrationContext integrationContext) {
        System.out.println("同意休假！！！！！！！！！！！！！！！！");


        IntegrationContextImpl integrationContext1 = new IntegrationContextImpl();
        integrationContext1.addInBoundVariable("approved",true);
        integrationContext1.addOutBoundVariable("approved",true);
        return integrationContext1;
    }


}
