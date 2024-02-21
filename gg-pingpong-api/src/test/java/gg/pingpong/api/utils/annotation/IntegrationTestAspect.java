package gg.pingpong.api.utils.annotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gg.server.domain.rank.redis.RankRedisRepository;

@Aspect
@Component
public class IntegrationTestAspect {

	@Autowired
	private RankRedisRepository rankRedisRepository;

	/**
	 * 통합테스트 종료 후 redis 데이터 삭제
	 */
	@After("execution(* *(..)) && @within(integrationTest)")
	public void afterTest(JoinPoint joinPoint, IntegrationTest integrationTest) {
		rankRedisRepository.deleteAll();
	}
}
