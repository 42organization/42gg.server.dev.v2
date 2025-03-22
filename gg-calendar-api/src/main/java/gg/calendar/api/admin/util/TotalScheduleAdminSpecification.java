package gg.calendar.api.admin.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import gg.data.calendar.PublicSchedule;

public class TotalScheduleAdminSpecification {

	public static Specification<PublicSchedule> searchByField(String content, LocalDate startTime, LocalDate endTime,
		String field) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			LocalDateTime startDateTime = startTime.atStartOfDay();
			LocalDateTime endDateTime = endTime.plusDays(1).atStartOfDay();

			Predicate startInRange = criteriaBuilder.and(
				criteriaBuilder.greaterThanOrEqualTo(root.get("startTime"), startDateTime),
				criteriaBuilder.lessThan(root.get("startTime"), endDateTime)
			);

			Predicate endInRange = criteriaBuilder.and(
				criteriaBuilder.greaterThanOrEqualTo(root.get("endTime"), startDateTime),
				criteriaBuilder.lessThan(root.get("endTime"), endDateTime)
			);

			Predicate spanningRange = criteriaBuilder.and(
				criteriaBuilder.lessThanOrEqualTo(root.get("startTime"), startDateTime),
				criteriaBuilder.greaterThanOrEqualTo(root.get("endTime"), endDateTime)
			);

			predicates.add(criteriaBuilder.or(startInRange, endInRange, spanningRange));

			if (content != null && field != null) {
				if ("classification".equals(field)) {
					Expression<String> enumAsString = root.get(field).as(String.class);
					predicates.add(
						criteriaBuilder.like(criteriaBuilder.lower(enumAsString), "%" + content.toLowerCase() + "%")
					);
				} else {
					Path<String> dynamicField = root.get(field);
					predicates.add(
						criteriaBuilder.like(criteriaBuilder.lower(dynamicField), "%" + content.toLowerCase() + "%")
					);
				}
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
