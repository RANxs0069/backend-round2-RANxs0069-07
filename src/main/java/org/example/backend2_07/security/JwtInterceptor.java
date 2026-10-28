package org.example.backend2_07.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
//这是预处理器，http请求经过这些步骤被鉴权等，传递给controller或者直接打回
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {
        // 1. 从请求头取 Token
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            writeUnauthorized(response);
            return false;   // 拦截没有Token的请求
        }
        String token = header.substring(7);   // 去掉 "Bearer " 这 7 个字符

        // 2. 校验 Token 是否有效（过期、篡改、格式错误都会返回 false）
        if (!jwtUtil.validateToken(token)) {
            writeUnauthorized(response);
            return false;   // Token 无效，拦截
        }

        String uri = request.getRequestURI();
        Claims claims = jwtUtil.parseToken(token);

        //3.检验用户身份。当用户访问如下受限网址时，解析token中携带的身份字段并鉴权
        String role = claims.get("role", String.class);
        if(uri.startsWith("/user/admin") && !"ADMIN".equals(role)) {
            writeForbidden(response);
            return false;
        }

        //4.每次通过鉴权的请求都检查是否临期，临期则自动补发新 token。token中的过期时间将会被更新，其他内容不改变
        if (jwtUtil.shouldRefresh(token)) {
            String newToken = jwtUtil.generateToken(
                    claims.get("userId", Long.class),
                    claims.getSubject(),
                    claims.get("role", String.class)
            );
            response.setHeader("X-New-Token", newToken);
        }

        // 解析token中的用户信息，放进 request，供 Controller 取用，这样可以减少查数据库（慢）
        request.setAttribute("userId", claims.get("userId", Long.class));
        request.setAttribute("role", claims.get("role", String.class));
        request.setAttribute("username", claims.getSubject());

        return true;   // 通过放行
    }

    /*
     可复用的写出响应方法，格式与ResponseEntity保持一致
     */
    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(401);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(
                "{\"msg\":\"未登录或token失效\",\"data\":null}"
        );
    }

    private void writeForbidden(HttpServletResponse response) throws IOException {
        response.setStatus(403);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(
                "{\"msg\":\"当前没有权限执行该操作\",\"data\":null}"
        );
    }
}
