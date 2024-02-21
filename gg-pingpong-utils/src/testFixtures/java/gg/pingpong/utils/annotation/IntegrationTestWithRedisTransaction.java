package gg.pingpong.utils.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.gg.server.utils.config.MySQLInitializer;
import com.gg.server.utils.config.RedisInitializer;

/**
 * 통합테스트 환경의 의존성 관리를 위한 어노테이션.
 *
 * <p>
 *   기본적으로 redis 트랜잭션을 false로 처리하는 설정을 Import 한다.
 * </p>
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@ContextConfiguration(initializers = {RedisInitializer.class, MySQLInitializer.class})
@Tag(TestTypeConstant.INTEGRATION_TEST)
public @interface IntegrationTestWithRedisTransaction {

}
