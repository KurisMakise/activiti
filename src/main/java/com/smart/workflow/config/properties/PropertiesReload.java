package com.smart.workflow.config.properties;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author k.makise
 */
//@Component
public class PropertiesReload extends PropertySourcesPlaceholderConfigurer {


    private static final String ZOOKEEPER_SERVER = "zkServer:2181";
    private static final int SESSION_TIMEOUT = 10000;

    private static final String COMMON_CONF_PATH = "/conf/common";
    private static final String APP_CONF_PATH = "/conf/app";
    private ZooKeeper client;

    private Properties properties;


    /**
     * 读取zookeeper数据到配置属性
     */
    @Override
    protected Properties mergeProperties() throws IOException {
        properties = super.mergeProperties();
        appendZookeeperProperties();
        try {
            client.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return properties;
    }

    private void appendZookeeperProperties() {

        try {
            client = new ZooKeeper(ZOOKEEPER_SERVER, SESSION_TIMEOUT, (event) -> {

            });
            appendProperties(COMMON_CONF_PATH);
            appendProperties(APP_CONF_PATH);
        } catch (KeeperException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 加载zookeeper路径下配置
     *
     * @param path 路径
     * @throws KeeperException
     * @throws InterruptedException
     */
    private void appendProperties(String path) throws KeeperException, InterruptedException {
        List<String> commonConfKeys = client.getChildren(path, true);
        for (String key : commonConfKeys) {
            byte[] data = client.getData(path + "/" + key, true, null);
            if (data == null) {
                continue;
            }
            Config config = JSON.parseObject(new String(data, UTF_8), Config.class);
            properties.put(config.getKey(), config.getValue());
        }
    }

    @Data
    private static class Config {
        private String key;
        private String value;
    }


}
