package com.smart.workflow.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author violet
 * @version 1.0
 * @date 2021/02/03 13:45
 */
@Slf4j
@Component
public class CsrfTokenRepositoryImpl implements CsrfTokenRepository {
    public static final String CSRF_HEADER_NAME = "X-CSRF-TOKEN";
    public static final String CSRF_PARAM_NAME = "_csrf";

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public CsrfToken generateToken(HttpServletRequest httpServletRequest) {
        String token = UUID.randomUUID().toString();
        log.debug("csrf filter: redis csrf repository:generate token:{}", token);

        return new DefaultCsrfToken(CSRF_HEADER_NAME, CSRF_PARAM_NAME, token);
    }

    @Override
    public void saveToken(CsrfToken csrfToken, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        redisTemplate.opsForValue().set("user", csrfToken.getToken());

        httpServletResponse.setHeader(CSRF_PARAM_NAME, csrfToken.getToken());
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader(CSRF_PARAM_NAME);
        if (token == null) {
            return null;
        }

        log.debug("csrf filter: redis csrf repository: load token{}", token);

        return new DefaultCsrfToken(CSRF_HEADER_NAME, CSRF_PARAM_NAME, token);
    }


}
