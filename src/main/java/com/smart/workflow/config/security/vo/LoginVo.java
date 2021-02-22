package com.smart.workflow.config.security.vo;

import lombok.Data;

/**
 * @author violet
 * @version 1.0
 * @date 2021/02/04 15:39
 */
@Data
public class LoginVo {
    public LoginVo() {

    }

    public LoginVo(String currentAuthority) {
        this.currentAuthority = currentAuthority;
        this.success = true;
    }

    private String type;
    /**
     * 角色权限
     */
    private String currentAuthority;
    /**
     * 登录成功
     */
    private boolean success;


}
