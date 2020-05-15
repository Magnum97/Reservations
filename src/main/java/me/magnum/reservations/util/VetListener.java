package me.magnum.reservations.util;

import me.magnum.reservations.Reservations;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import static me.magnum.reservations.util.DataWorks.dropIn;

public class VetListener implements Listener {

	private final String pre = Reservations.getPre();
	private final String waiting = Reservations.getCfg().getString("messages.waiting");
	private final DataWorks dw = new DataWorks();

	@EventHandler
	public void onJoin (PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission("horserpg.vet")) {
			dw.addVet(event.getPlayer());
			if (dropIn.size() > 0) {
				BukkitRunnable notice = new BukkitRunnable() {
					@Override
					public void run () {
						event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
								pre + waiting));
					}
				};
				notice.runTaskLater(Reservations.getPlugin(), 20 * 10);
			}
		}
	}

	@EventHandler
	public void onQuit (PlayerQuitEvent event) {
		if (event.getPlayer().hasPermission("horserpg.vet"))
			dw.removeVet(event.getPlayer());
	}

}
