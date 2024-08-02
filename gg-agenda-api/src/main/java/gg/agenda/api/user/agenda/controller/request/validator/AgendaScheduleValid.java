package gg.agenda.api.user.agenda.controller.request.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = AgendaScheduleValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AgendaScheduleValid {

	String message() default "올바르지 않은 대회 일정입니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
