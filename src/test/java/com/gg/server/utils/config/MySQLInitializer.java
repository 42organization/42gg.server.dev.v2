package com.gg.server.utils.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MySQLContainer;

/**
 * MysqlInitializer.
 *
 * <p>
 *  mysql testContainer 초기화 작업을 수행
 * </p>
 *
 * @author : middlefitting
 * @since : 2024/01/09
 */
public class MySQLInitializer implements
    ApplicationContextInitializer<ConfigurableApplicationContext> {
  private static final String params = "?serverTimezone=Asia/Seoul&characterEncoding=UTF-8";
  private static final MySQLContainer<?> MYSQL_CONTAINER =
      new MySQLContainer<>("mysql:8.0")
          .withDatabaseName("test")
          .withUsername("root")
          .withPassword("1234");

  static {
    MYSQL_CONTAINER.start();
  }

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    TestPropertyValues.of(
        "spring.datasource.url=" + MYSQL_CONTAINER.getJdbcUrl() + params,
        "spring.datasource.username=" + MYSQL_CONTAINER.getUsername(),
        "spring.datasource.password=" + MYSQL_CONTAINER.getPassword()
    ).applyTo(applicationContext.getEnvironment());
  }
}
