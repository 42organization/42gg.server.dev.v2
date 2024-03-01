package gg.pingpong.api.global.log.aspect;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import gg.pingpong.api.global.log.domain.TraceStatus;
import gg.pingpong.api.global.log.service.LogTrace;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@AllArgsConstructor
@Component
public class TraceAspect {
	private final LogTrace logTrace;

	@Pointcut("execution(* gg.pingpong.api.admin..*(..))")
	public void allAdmin() {
	}

	@Pointcut("execution(* gg.pingpong.api.user..*(..))")
	public void allDomain() {
	}

	@Pointcut("execution(* gg.pingpong.api.global.security..*(..))")
	public void securityDomain() {
	}

	@Pointcut("execution(* gg.pingpong.api.global.scheduler..*(..))")
	public void scheduler() {
	}

	@Around("(allAdmin() || allDomain() || scheduler()) && !securityDomain()")
	public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
		TraceStatus status = null;
		MethodSignature method = (MethodSignature)joinPoint.getSignature();
		Object[] methodArgs = joinPoint.getArgs();
		try {
			status = logTrace.begin(
				method.getDeclaringType().getSimpleName() + "." + method.getName() + "(): arguments = "
					+ Arrays.toString(methodArgs));
			Object result = joinPoint.proceed();
			logTrace.end(status);
			return result;
		} catch (Exception e) {
			logTrace.exception(status, e);
			throw e;
		}
	}
}
