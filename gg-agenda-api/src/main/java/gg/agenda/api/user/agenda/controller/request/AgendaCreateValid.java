package gg.agenda.api.user.agenda.controller.request;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = AgendaCreateValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AgendaCreateValid {
	String message() default "유효하지 않은 대회 정보입니다.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
