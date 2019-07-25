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
	boolean canceled;
	
	public Appointment () {
	}
	
	public Appointment (LocalDateTime time, String playerId, String reason) {
		this.playerId = playerId;
		this.time = time;
		this.reason = reason;
		canceled = false;
	}
	
	
	@Override
	public int compareTo (Appointment a) {
		return this.getTime().compareTo(a.getTime());
	}
	
}
