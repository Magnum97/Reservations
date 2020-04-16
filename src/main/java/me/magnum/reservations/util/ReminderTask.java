package me.magnum.reservations.util;

import me.magnum.reservations.Reservations;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Common;

import static me.magnum.reservations.util.DataWorks.onlineVets;
import static me.magnum.reservations.util.DataWorks.walkIns;

public class ReminderTask extends BukkitRunnable {
	Reservations plugin = Reservations.getPlugin();
	SimpleConfig cfg = plugin.getCfg();
	String pre = cfg.getString("plugin-prefix");
	String waiting = cfg.getString("messages.waiting");
	public ReminderTask () {
		run();
	}
	
	@Override
	public void run () {
		if (walkIns.size() > 0) {
			for (Player p : onlineVets) {
				Common.tell(p, pre + waiting);
			}
		}
	}
}
