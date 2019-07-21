package me.magnum.reservations.type;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class Appointment implements Serializable {
	
	LocalDateTime time;
	String reason;
	String playerId; // todo remove if not used
	
	public Appointment () {
	}
	
	public Appointment (LocalDateTime time, String playerId) {
		this.time = time;
		this.playerId = playerId;
	}

	public Appointment (LocalDateTime time, String playerId, String reason) {
		this.playerId = playerId;
		this.time = time;
		this.reason = reason;
	}
}
