package gg.agenda.api.user.agenda.controller.request.validator;

import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import gg.agenda.api.user.agenda.controller.request.AgendaCreateReqDto;

public class AgendaCapacityValidator implements ConstraintValidator<AgendaCapacityValid, AgendaCreateReqDto> {

	@Override
	public void initialize(AgendaCapacityValid constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(AgendaCreateReqDto value, ConstraintValidatorContext context) {
		if (Objects.isNull(value)) {
			return true;
		}
		return mustHaveValidTeam(value);
	}

	private boolean mustHaveValidTeam(AgendaCreateReqDto value) {
		return value.getAgendaMinTeam() <= value.getAgendaMaxTeam()
			&& value.getAgendaMinPeople() <= value.getAgendaMaxPeople();
	}
}
