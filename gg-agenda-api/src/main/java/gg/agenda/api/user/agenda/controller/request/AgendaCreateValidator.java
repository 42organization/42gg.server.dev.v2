package gg.agenda.api.user.agenda.controller.request;

import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AgendaCreateValidator implements ConstraintValidator<AgendaCreateValid, AgendaCreateReqDto> {

	@Override
	public void initialize(AgendaCreateValid constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(AgendaCreateReqDto value, ConstraintValidatorContext context) {
		if (Objects.isNull(value)) {
			return true;
		}
		return mustHaveValidSchedule(value) && mustHaveValidTeam(value);
	}

	private boolean mustHaveValidSchedule(AgendaCreateReqDto value) {
		return value.getAgendaDeadLine().isBefore(value.getAgendaStartTime())
			&& value.getAgendaStartTime().isBefore(value.getAgendaEndTime());
	}

	private boolean mustHaveValidTeam(AgendaCreateReqDto value) {
		return value.getAgendaMinTeam() < value.getAgendaMaxTeam()
			&& value.getAgendaMinPeople() < value.getAgendaMaxPeople();
	}
}
