package com.smart.workflow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Lazy;

/**
 * @author kurisu makise
 * @version 1.0
 * @date 2020/9/24 16:33
 */
@SpringBootApplication
@EnableFeignClients
@MapperScan(basePackages = "com.smart.workflow.mapper")
public class ActivitiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ActivitiApplication.class);
    }



}
