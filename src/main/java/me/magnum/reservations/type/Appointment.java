package me.magnum.reservations.type;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalTime;

@Getter
@Setter
public class Appointment implements Serializable, Comparable <Appointment> {

	LocalTime time;
	String reason;
	String playerId;
	int number;
	long created;

	public Appointment () {
		created = System.currentTimeMillis();
	}

	public Appointment (LocalTime time, String playerId, String reason) {
		this.playerId = playerId;
		this.time = time;
		this.reason = reason;
		created = System.currentTimeMillis();
	}

	public Appointment (LocalTime time, String playerId, String reason, int number) {
		this.time = time;
		this.reason = reason;
		this.playerId = playerId;
		this.number = number;
		created = System.currentTimeMillis();
	}

	@Override
	public int compareTo (Appointment a) {
		return this.getTime().compareTo(a.getTime());
	}

}
