package gg.pingpong.api.global.log.pointcut;

import org.aspectj.lang.annotation.Pointcut;

public class TracePointcut {
	@Pointcut("execution(* gg.pingpong.api.user..*(..))")
	public void allDomain() {
	}

	//타입 패턴이 *Service
	@Pointcut("execution(* *..*Service.*(..))")
	public void allService() {
	}

	//allOrder && allService
	@Pointcut("allDomain() && allService()")
	public void orderAndService() {
	}
}
