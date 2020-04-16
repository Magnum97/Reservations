package me.magnum.reservations.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import me.magnum.reservations.Reservations;
import me.magnum.reservations.util.DataWorks;
import me.magnum.reservations.util.SimpleConfig;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;



@CommandAlias ("%command")
public class Reservation extends BaseCommand {

	private SimpleConfig cfg = Reservations.getPlugin().getCfg();
private String pre = cfg.getString("plugin-prefix");

	public Reservation () {
	}

	@Subcommand ("make|call")
	@Description ("Make a reservation and get a number")
	@CommandCompletion ("@players")
	@CommandPermission ("reservations.make.self")
	public void onMake (CommandSender sender, @Default ("") String player, @Default ("") String time, @Default ("") String reason) throws IllegalAccessException {
		if (sender instanceof CommandBlock) {
			return;
		}
		if (player.equals("")) {
			if (sender instanceof ConsoleCommandSender) {
				Common.tell(sender, pre + "I'm sorry console, you can't make an appointment for yourself.");
				// Common.tell(sender,  help .getCurrentCommandManager().generateCommandHelp("make");
				return;
			}
			else {
				player = sender.getName();
			}
		}
		DataWorks dw = new DataWorks();
		String result;
		if (sender instanceof Player) {
			if ((! (sender.hasPermission("reservations.make.others"))) && (! (sender.getName().equalsIgnoreCase(player)))) {
				Common.tell(sender, pre + cfg.getString("messages.no-permission"));
				return;
			}
		}
		if (time.length() > 0) {
			if (! time.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9](\\b|[a|p])")) {
				// getCurrentCommandManager().generateCommandHelp("make");
				Common.tell(sender, pre + time + "&e is not a valid time."
						, pre + "&bPlease format time:&e HH:mm &bYou can use 24 hour time or 12 hour with &ea&7/&ep&b"
						, pre + "&bFor example &f15:30 &b| &f3:30p &bor &f03:30&b | &f3:30a");
				return;
			}
			else {
				if (dw.hasApt(player)) {
					Common.tell(sender, pre + cfg.getString("messages.update-apt").replace("%player%", player));
					dw.updateApt(dw.getApt(player), time, reason);
					return;
				}
				result = dw.makeAppt(player, time, reason);
				Common.tell(sender, pre + result);
			}
		}
		if (sender instanceof Player) {
			((Player) sender).playSound(((Player) sender).getLocation(), Sound.BLOCK_NOTE_BELL, 1.0F, 1.0F);
		}
		if (dw.checkNumber(player)) {
			Common.tell(sender, pre + cfg.getString("messages.has-appt"));
			return;
		}
		result = dw.make(player);
		if (result.contains(sender.getName())) {
			result = result.replace(sender.getName(), "You");
		}
		Common.tell(sender, pre + result);
		Common.log(cfg.getString("messages.log-confirm").replaceAll("%player%", player));

		int v = 0;
		for (Player p : Bukkit.getOnlinePlayers()) { //todo change to read from onlineVet list
			if (p.hasPermission("reservations.notify")) {
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BELL, 1.0F, 1.0F);
				Common.tell(p, pre + cfg.getString("messages.player-confirm").replaceAll("%player%", player));
				v++;
			}
		}
		if (sender instanceof Player) { //todo change to config based message.
			Common.tell(sender, pre + "There are " + v + " vets online right now.");
		}
	}

	@Subcommand ("view|list")
	@Description ("View current reservations")
	@CommandPermission ("reservations.view")
	public void onView (CommandSender sender, @Default ("all") String type) {
		if (sender instanceof CommandBlock) {
			return;
		}
		DataWorks dw = new DataWorks();
		if ((type.equalsIgnoreCase("waiting") ||
				type.equalsIgnoreCase("all"))) {
			dw.view(sender);
		}
		if (type.equalsIgnoreCase("apt") ||
				type.equalsIgnoreCase("all")) {
			dw.listAppointments(sender);
		}
	}

	@Subcommand ("clear")
	@Description ("Clear a reservation from the list")
	@CommandPermission ("reservations.clear")
	public void onClear (CommandSender sender, int key) {
		DataWorks dw = new DataWorks();
		String result = dw.clear(key);
		Common.tell(sender, pre + result);
	}

	@Subcommand ("cancel")
	@Description ("Cancel an appointment")
	@CommandPermission ("reservations.cancel.self")
	public void onCancel (CommandSender sender, @Default ("") String player) throws IllegalAccessException {
		if (sender instanceof CommandBlock) {
			return;
		}
		DataWorks dw = new DataWorks();
		String result;
		if (player.equals("")) {
			if (sender instanceof ConsoleCommandSender) {
				Common.tell(sender, pre + "There are no appointments for console.");
				return;
			}
			else {
				player = sender.getName();
			}
		}
		if (sender instanceof Player) {
			if ((! (sender.hasPermission("reservations.make.others"))) && (! (sender.getName().equalsIgnoreCase(player)))) {
				Common.tell(sender, pre + cfg.getString("messages.no-cancel-other"));
				return;
			}
		}

		if (dw.hasApt(player)) {
			dw.cancelApt(dw.getApt(player));
			Common.tell(sender, pre + cfg.getString("messages.canceled").replace("%player%", player));
		}
		else {
			Common.tell(sender, pre + cfg.getString("messages.no-appointments").replace("%player%", player));
		}
	}

	@Subcommand ("wipe")
	@Description ("Wipe the list clean baby!")
	@CommandPermission ("reservations.clear.all")
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
