package me.magnum.reservations.type;

import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public class Appointment {
	
	@Getter String playerId;
	@Getter OfflinePlayer player;
	@Getter LocalDateTime time;
	@Getter String reason;
	
	public Appointment (OfflinePlayer player, LocalDateTime time) {
		this.player = player;
		this.time = time;
	}
	
	public Appointment (Player player, LocalDateTime time, String reason) {
		this.player = player;
		this.time = time;
		this.reason = reason;
	}
}
