package com.itheima.classroomsigninbackend.interceptor;

import com.itheima.classroomsigninbackend.common.ResultCodeEnum;
import com.itheima.classroomsigninbackend.exception.BusinessException;
import com.itheima.classroomsigninbackend.utils.JwtUtils;
import com.itheima.classroomsigninbackend.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || authorization.isBlank()) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED);
        }
        String token = authorization.startsWith("Bearer ")
            ? authorization.substring(7)
            : authorization;
        try {
            String userId = JwtUtils.getUserIdFromToken(token);
            if (userId == null || userId.isBlank()) {
                throw new BusinessException(ResultCodeEnum.UNAUTHORIZED);
            }
            UserContext.setUserId(userId);
            return true;
        } catch (Exception ex) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
