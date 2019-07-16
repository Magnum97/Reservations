package me.magnum.reservations.util;

import me.magnum.lib.Common;
import me.magnum.reservations.Reservations;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import static me.magnum.reservations.util.Config.pre;
import static me.magnum.reservations.util.Config.waiting;
import static me.magnum.reservations.util.DataWorks.onlineVets;

public class ReminderTask extends BukkitRunnable {
	
	private JavaPlugin plugin;
	
	
	private void ReminderTask (Reservations instance) {
	}
	
	@Override
	public void run () {
		for (Player p : onlineVets) {
			Common.tell(p, pre + waiting);
		}
	}
}
