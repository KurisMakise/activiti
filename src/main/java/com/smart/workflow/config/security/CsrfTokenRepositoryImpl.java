package com.smart.workflow.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

/**
 * token执行流程{@link org.springframework.security.web.csrf.CsrfFilter} doFilterInternal
 * 首先读取token，token不存在生成token
 * 验证服务器缓存token与页面token是否一致
 * 验证完成 重新生成token保存
 *
 * @author violet
 * @version 1.0
 * @date 2021/02/03 13:45
 */
@Slf4j
@Component
public class CsrfTokenRepositoryImpl implements CsrfTokenRepository {
    public static final String CSRF_HEADER_NAME = "X-XSRF-TOKEN";
    public static final String CSRF_PARAM_NAME = "_csrf";

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * {@link org.springframework.security.web.csrf.CsrfFilter}
     *
     * @param httpServletRequest
     * @return
     */
    @Override
    public CsrfToken generateToken(HttpServletRequest httpServletRequest) {
        String token = UUID.randomUUID().toString();
        log.info("csrf filter: redis csrf repository:generate token:{}", token);

        return new DefaultCsrfToken(CSRF_HEADER_NAME, CSRF_PARAM_NAME, token);
    }

    @Override
    public void saveToken(CsrfToken csrfToken, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        redisTemplate.opsForValue().set("user", csrfToken.getToken());

        httpServletResponse.setHeader(CSRF_PARAM_NAME, csrfToken.getToken());
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest httpServletRequest) {
        String token = (String) redisTemplate.opsForValue().get("user");
        if (token == null) {
            return null;
        }

        log.info("csrf filter: redis csrf repository: load token{}", token);

        return new DefaultCsrfToken(CSRF_HEADER_NAME, CSRF_PARAM_NAME, token);
    }
}
