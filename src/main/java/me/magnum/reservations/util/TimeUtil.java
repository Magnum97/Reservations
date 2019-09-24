package me.magnum.reservations.util;

import java.time.Duration;
import java.time.Instant;

class TimeUtil {

	TimeUtil () {
	}

	public String getWaitTime (Instant from) {
		Instant now = Instant.now();
		Duration between = Duration.between(from, now);
		return String.format("%02d H %02d m", between.toHours(), between.toMinutes());
	}
}
