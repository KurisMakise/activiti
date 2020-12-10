package com.smart.workflow.config.rpc;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * <p>
 * 配置feign头信息
 * </p>
 *
 * @author violet
 * @version 1.0
 * @since 2020/2/24 21:20
 */
@Configuration
public class FeignConfig {
//    文件上传
//    @RequestMapping(value = "/uploadImage", method = RequestMethod.POST, produces = {
//            MediaType.APPLICATION_JSON_UTF8_VALUE }, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public String uploadImageAndCrtThumbImage(@RequestPart MultipartFile multipartFile);

    private static final String COOKIE = "Cookie";
    private static final String CONTENT_TYPE = "Content-Type";

    @Bean
    public RequestInterceptor requestInterceptor() {

        return template -> {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames == null) {
                return;
            }
            //添加头信息cookie信息
            template.header(COOKIE, request.getHeader(COOKIE));
            template.header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
         };
    }
}
