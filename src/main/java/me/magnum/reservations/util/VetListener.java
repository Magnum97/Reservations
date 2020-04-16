package me.magnum.reservations.util;

import me.magnum.reservations.Reservations;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Common;
import static me.magnum.reservations.util.DataWorks.walkIns;

public class VetListener implements Listener {
	private final Reservations plugin = Reservations.getPlugin();
	private SimpleConfig cfg = plugin.getCfg();
	private String pre = cfg.getString("plugin-prefix");
	private DataWorks dw = new DataWorks();
	
	@EventHandler
	public void onJoin (PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission("horserpg.vet")) {
			dw.addVet(event.getPlayer());
			if (walkIns.size() > 0) {
				BukkitRunnable notice = new BukkitRunnable() {
					@Override
					public void run () {
						Common.tell(event.getPlayer(), pre + cfg.getString("messages.waiting"));
					}
				};
				notice.runTaskLater(Reservations.getPlugin(), 20 * 10);
			}
		}
	}
	
	@EventHandler
	public void onQuit (PlayerQuitEvent event) {
		dw.removeVet(event.getPlayer());
		
	}
	
}
