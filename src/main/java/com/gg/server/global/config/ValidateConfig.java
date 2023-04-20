package com.gg.server.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;

@Configuration
public class ValidateConfig {
    @Bean
    public ExecutableValidator executableValidator(){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        return validatorFactory.getValidator().forExecutables();
    }
}
