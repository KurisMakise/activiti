package com.smart.workflow.controller;

import com.alibaba.fastjson.JSON;
import org.activiti.api.runtime.shared.security.SecurityManager;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author violet
 * @version 1.0
 * @date 2021/02/02 16:18
 */
@RestController
@RequestMapping()
public class UserController {

    @Autowired
    private SecurityManager securityManager;

    @GetMapping("currentUser")
    public Object currentUser() {
        return JSON.parse("{\n" +
                "    name: 'Serati Ma',\n" +
                "    avatar: 'https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png',\n" +
                "    userid: '00000001',\n" +
                "    email: 'antdesign@alipay.com',\n" +
                "    signature: '海纳百川，有容乃大',\n" +
                "    title: '交互专家',\n" +
                "    group: '蚂蚁集团－某某某事业群－某某平台部－某某技术部－UED',\n" +
                "    tags: [\n" +
                "      {\n" +
                "        key: '0',\n" +
                "        label: '很有想法的',\n" +
                "      },\n" +
                "      {\n" +
                "        key: '1',\n" +
                "        label: '专注设计',\n" +
                "      },\n" +
                "      {\n" +
                "        key: '2',\n" +
                "        label: '辣~',\n" +
                "      },\n" +
                "      {\n" +
                "        key: '3',\n" +
                "        label: '大长腿',\n" +
                "      },\n" +
                "      {\n" +
                "        key: '4',\n" +
                "        label: '川妹子',\n" +
                "      },\n" +
                "      {\n" +
                "        key: '5',\n" +
                "        label: '海纳百川',\n" +
                "      },\n" +
                "    ],\n" +
                "    notifyCount: 12,\n" +
                "    unreadCount: 11,\n" +
                "    country: 'China',\n" +
                "    geographic: {\n" +
                "      province: {\n" +
                "        label: '浙江省',\n" +
                "        key: '330000',\n" +
                "      },\n" +
                "      city: {\n" +
                "        label: '杭州市',\n" +
                "        key: '330100',\n" +
                "      },\n" +
                "    },\n" +
                "    address: '西湖区工专路 77 号',\n" +
                "    phone: '0752-268888888',\n" +
                "  }");
    }
}
