package com.gg.server.global.log.aspect;

import com.gg.server.global.log.domain.TraceStatus;
import com.gg.server.global.log.service.LogTrace;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@AllArgsConstructor
@Component
public class TraceAspect {
    private final LogTrace logTrace;

    @Pointcut("execution(* com.gg.server.admin..*(..))")
    public void allAdmin(){}

    @Pointcut("execution(* com.gg.server.domain..*(..))")
    public void allDomain(){}

    @Pointcut("execution(* com.gg.server.global.security..*(..))")
    public void securityDomain(){}

    @Pointcut("execution(* com.gg.server.global.utils..*(..))")
    public void util(){}

    @Pointcut("execution(* com.gg.server.global.scheduler..*(..))")
    public void scheduler(){}

    @Around("(allAdmin() || allDomain() || util() || scheduler()) && !securityDomain()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        TraceStatus status = null;
        MethodSignature method = (MethodSignature)joinPoint.getSignature();
        Object[] methodArgs = joinPoint.getArgs();
        try{
            status = logTrace.begin(method.getDeclaringType().getSimpleName() + "." + method.getName() + "(): arguments = " + Arrays.toString(methodArgs));
            Object result = joinPoint.proceed();
            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
