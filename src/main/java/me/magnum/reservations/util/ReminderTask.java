package me.magnum.reservations.util;

import me.magnum.reservations.Reservations;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.magnum.reservations.util.DataWorks.dropIn;
import static me.magnum.reservations.util.DataWorks.onlineVets;

public class ReminderTask extends BukkitRunnable {

	private final String pre = Reservations.getPre();
	private final String waiting = Reservations.getCfg().getString("messages.waiting");


	public ReminderTask () {
		run();
	}

	@Override
	public void run () {
		if (dropIn.size() > 0) {
			for (Player p : onlineVets) {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', pre + waiting));
			}
		}
	}
}
