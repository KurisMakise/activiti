package com.smart.workflow.config.security.handler;

import com.smart.workflow.config.security.utils.ResponseBodyUtils;
import com.smart.workflow.config.security.vo.LoginVo;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author violet
 * @version 1.0
 * @date 2021/02/04 14:24
 */
@Component
public class CusAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        ResponseBodyUtils.write(response, new LoginVo());
    }
}
