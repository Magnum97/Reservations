package me.magnum.reservations.type;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class Appointment implements Serializable, Comparable <Appointment> {
	
	LocalDateTime time;
	String reason;
	String playerId;
	int number;
	
	public Appointment () {
	}
	
	public Appointment (LocalDateTime time, String playerId, String reason) {
		this.playerId = playerId;
		this.time = time;
		this.reason = reason;
	}
	
	public Appointment (LocalDateTime time, String playerId, String reason,  int number) {
		this.time = time;
		this.reason = reason;
		this.playerId = playerId;
		this.number = number;
	}
	
	@Override
	public int compareTo (Appointment a) {
		return this.getTime().compareTo(a.getTime());
	}
	
}
