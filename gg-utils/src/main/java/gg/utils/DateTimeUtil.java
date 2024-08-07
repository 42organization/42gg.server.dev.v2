package gg.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
	private static final ZoneId SEOUL_ZONE_ID = ZoneId.of("Asia/Seoul");
	private static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

	public static LocalDateTime convertToSeoulDateTime(String dateTimeString) {
		return ZonedDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
			.withZoneSameInstant(UTC_ZONE_ID)
			.withZoneSameInstant(SEOUL_ZONE_ID)
			.toLocalDateTime();
	}
}
