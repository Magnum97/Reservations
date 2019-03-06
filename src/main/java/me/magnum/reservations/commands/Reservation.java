package me.magnum.reservations.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import me.magnum.reservations.util.SimpleConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import static me.magnum.reservations.util.Config.*;

public class Reservation extends BaseCommand {
	
	
	private SimpleConfig data = new SimpleConfig("reservations.yml", false);
	
	
	@CommandAlias("%command")
	public void reservation (CommandSender sender, String player) {
		String uuid;
		OfflinePlayer p = Bukkit.getOfflinePlayer(player);
		if (p.hasPlayedBefore()) {
			uuid = p.getUniqueId().toString();
			next++;
			data.set("next-appointment", next);
			data.set(uuid, next);
			data.saveConfig();
			sender.sendMessage(pre + player + confirmAppt);
		}
		else {
			sender.sendMessage(player + " has never logged in.");
			return;
		}
	}
	
}
