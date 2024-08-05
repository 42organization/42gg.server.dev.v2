package gg.agenda.api.user.agenda.controller.request.validator;

import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import gg.agenda.api.user.agenda.controller.request.AgendaCreateReqDto;

public class AgendaScheduleValidator implements ConstraintValidator<AgendaScheduleValid, AgendaCreateReqDto> {

	@Override
	public void initialize(AgendaScheduleValid constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(AgendaCreateReqDto value, ConstraintValidatorContext context) {
		if (Objects.isNull(value)) {
			return true;
		}
		if (Objects.isNull(value.getAgendaDeadLine())
			|| Objects.isNull(value.getAgendaStartTime())
			|| Objects.isNull(value.getAgendaEndTime())) {
			return false;
		}
		return mustHaveValidSchedule(value);
	}

	private boolean mustHaveValidSchedule(AgendaCreateReqDto value) {
		return value.getAgendaDeadLine().isBefore(value.getAgendaStartTime())
			&& value.getAgendaStartTime().isBefore(value.getAgendaEndTime());
	}
}
