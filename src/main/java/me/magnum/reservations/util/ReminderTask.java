package me.magnum.reservations.util;

import me.magnum.lib.Common;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.magnum.reservations.util.Config.pre;
import static me.magnum.reservations.util.Config.waiting;
import static me.magnum.reservations.util.DataWorks.dropIn;
import static me.magnum.reservations.util.DataWorks.onlineVets;

public class ReminderTask extends BukkitRunnable {

	public ReminderTask () {
		run();
	}

	@Override
	public void run () {
		if (dropIn.size() > 0) {
			for (Player p : onlineVets) {
				Common.tell(p, pre + waiting);
			}
		}
	}
}
