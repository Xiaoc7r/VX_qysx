package com.itheima.classroomsigninbackend.aspect;

import java.util.Arrays;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Around("execution(* com.itheima.classroomsigninbackend.controller..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String requestPath = resolveRequestPath();
        Object[] args = joinPoint.getArgs();
        logger.info("Request start path={}, args={}", requestPath, Arrays.toString(args));
        try {
            Object result = joinPoint.proceed();
            long cost = System.currentTimeMillis() - startTime;
            logger.info("Request end path={}, costMs={}, result={}", requestPath, cost, result);
            return result;
        } catch (Throwable ex) {
            long cost = System.currentTimeMillis() - startTime;
            logger.error("Request error path={}, costMs={}, error={}", requestPath, cost, ex.toString());
            throw ex;
        }
    }

    private String resolveRequestPath() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            return servletAttributes.getRequest().getRequestURI();
        }
        return "N/A";
    }
}
