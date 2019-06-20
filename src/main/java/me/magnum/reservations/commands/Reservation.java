package me.magnum.reservations.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import me.magnum.lib.CheckSender;
import me.magnum.lib.Common;
import me.magnum.reservations.Reservations;
import me.magnum.reservations.util.Config;
import me.magnum.reservations.util.DataWorks;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.magnum.reservations.util.Config.pre;

@CommandAlias("%command")
public class Reservation extends BaseCommand {
	
	
	public Reservation () {
	}
	
	@Subcommand("make")
	@Description("Make a reservation and get a number")
	@CommandCompletion("@players")
	@CommandPermission("reservations.make.self")
	public void onMake (CommandSender sender, @Optional String player) {
		if (player == null) {
			player = sender.getName();
		}
		DataWorks dw = new DataWorks();
		if (sender instanceof Player) {
			if ((!(sender.hasPermission("reservations.make.others"))) && (!(sender.getName().equalsIgnoreCase(player)))) {
				Common.tell(sender, pre + Config.noMakeOther);
				return;
			}
			((Player) sender).playSound(((Player) sender).getLocation(), Sound.BLOCK_NOTE_BELL, 1.0F, 1.0F);
		}
		if (dw.check(player)) {
			Common.tell(sender, pre + Config.hasAppt);
			return;
		}
		String result = dw.make(player);
		Common.tell(sender, pre + result);
		Common.setInstance(Reservations.getPlugin());
		Common.log(pre + Config.logConfirm.replaceAll("%player%", player));
		// Common.tell(sender, pre + "Appointment made for " + player); /* Un needed with log message ? */
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.hasPermission("reservations.notify")) {
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BELL, 1.0F, 1.0F);
				Common.tell(p, pre + Config.playerConfirm.replaceAll("%player%", player));
				
			}
		}
	}
	
	@Subcommand("view|list")
	@Description("View current reservations")
	@CommandPermission("reservations.view")
	public void onView (CommandSender sender) {
		if (CheckSender.isCommand(sender)) {
			return;
		}
		DataWorks dw = new DataWorks();
		// LinkedHashMap <Integer, String> result; /* changed method - this is not needed */
		dw.view(sender);
	}
	
	@Subcommand("clear")
	@Description("Clear a reservation from the list")
	@CommandPermission("reservations.clear")
	public void onClear (CommandSender sender, int key) {
		DataWorks dw = new DataWorks();
		String result = dw.clear(key);
		Common.tell(sender, pre + result);
	}
	
	@Subcommand("wipe")
	@Description("Wipe the list clean baby!")
	@CommandPermission("reservations.clear.all")
	public void onWipe (CommandSender sender, @Optional String confirm) {
		if (confirm == null) {
			confirm = "";
		}
		if (confirm.equalsIgnoreCase("confirm")) {
			DataWorks dw = new DataWorks();
			dw.wipe(sender);
		}
		else {
			Common.tell(sender, pre + "&cYou are about to clear the list.",
			            pre + "&eTo confirm add &6confirm&e after the command.");
		}
	}
	
	@HelpCommand
	public void onHelp (CommandSender sender, CommandHelp help) {
		help.showHelp();
	}
}
