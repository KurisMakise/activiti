package com.smart.workflow.config.security.handler;

import com.smart.workflow.config.security.utils.ResponseBodyUtils;
import com.smart.workflow.config.security.vo.LoginVo;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
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
public class CusAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        ResponseBodyUtils.write(response, new LoginVo(request.getParameter("username")));
    }
}
