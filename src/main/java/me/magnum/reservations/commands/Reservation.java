package me.magnum.reservations.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import me.magnum.reservations.util.SimpleConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import static me.magnum.reservations.util.Config.*;

@CommandAlias("%command")
public class Reservation extends BaseCommand {
	
	
	private SimpleConfig data = new SimpleConfig("reservations.yml", false);
	
	
	@Subcommand("make")
	@Description("Get a number")
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
		}
	}
	
	@Subcommand("view")
	@Description("View current reservations")
	@CommandPermission("%command.view")
	public void onView (CommandSender sender) {
	
	}
	
	@Subcommand("clear")
	@Description("Clear a reservation from the list")
	@CommandPermission("%command.clear")
	public void onClear (CommandSender sender, String player) {
		String uuid;
		OfflinePlayer p = Bukkit.getOfflinePlayer(player);
		uuid = p.getUniqueId().toString();
		if (data.getString(uuid) != null) {
			uuid = p.getUniqueId().toString();
			data.set(uuid, null);
			data.saveConfig();
			sender.sendMessage(pre + player + " has been removed from the queue");
		}
		else {
			sender.sendMessage(pre + player + " was not found in the queue");
		}
	}
}
