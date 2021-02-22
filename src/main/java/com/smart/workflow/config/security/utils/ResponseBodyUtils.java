package com.smart.workflow.config.security.utils;

import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author violet
 * @version 1.0
 * @date 2021/02/04 15:45
 */
public class ResponseBodyUtils {

    /**
     * 您不应该关闭流。在servlet完成作为servlet请求生命周期的一部分运行之后，servlet容器将自动关闭流。
     * 例如，如果您关闭流，那么在实现Filter的情况下将不可用
     * <p>
     * 是否需要 flush the servlet outputstream?
     * 不用了 servletcontainer将为您刷新并关闭它。顺便说一句，关闭已经隐式调用了flush。
     *
     * @param response 输出流
     * @param data     对象
     * @throws IOException io异常
     */
    public static void write(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        response.getWriter().write(JSON.toJSONString(data));
    }
}
