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

@SuppressWarnings("deprecation")
@CommandAlias("%command")
public class Reservation extends BaseCommand {
	
	
	public Reservation () {
	}
	
	@Subcommand("make|call")
	@Description("Make a reservation and get a number")
	@CommandCompletion("@players")
	@CommandPermission("reservations.make.self")
	public void onMake (CommandSender sender, @Default("") String player, @Default("") String time, @Default("") String reason) {
		if (CheckSender.isCommand(sender)) {
			return;
		}
		DataWorks dw = new DataWorks();
		String result;
		if (player.equals("")) {
			if ((CheckSender.isConsole(sender))) {
				Common.tell(sender, pre + "I'm sorry console, you can't make an appointment for yourself.");
				// Common.tell(sender,  help .getCurrentCommandManager().generateCommandHelp("make");
				return;
			}
			player = sender.getName();
		}
		if (sender instanceof Player) {
			if ((!(sender.hasPermission("reservations.make.others"))) && (!(sender.getName().equalsIgnoreCase(player)))) {
				Common.tell(sender, pre + Config.noMakeOther);
				return;
			}
			((Player) sender).playSound(((Player) sender).getLocation(), Sound.BLOCK_NOTE_BELL, 1.0F, 1.0F);
		}
		if (time.length() > 0) {
			if (!time.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]")) {
				// getCurrentCommandManager().generateCommandHelp("make");
				Common.tell(sender, pre + time + "&e is not a valid time."
						, pre + "&bPlease format time:&e HH:mm &bYou can use 24 hour time or 12 hour with &ea&7/&ep&b"
						, pre + "&bFor example &f15:30 &b| &f3:30p &bor &f03:30&b | &f3:30a");
				return;
			}
			else {
				if (dw.checkApt(player)) {
					Common.tell(sender, pre + Config.hasAppt);
					return;
				}
				else {
					result = dw.makeAppt(player, time, reason);
					Common.tell(sender, pre + result);
					return;
				}
			}
		}
		
		if (dw.checkNumber(player)) {
			Common.tell(sender, pre + Config.hasAppt);
			return;
		}
		result = dw.make(player);
		if (result.contains(sender.getName())) {
			result = result.replace(sender.getName(), "You");
		}
		Common.tell(sender, pre + result);
		Common.setInstance(Reservations.getPlugin());
		Common.log(Config.logConfirm.replaceAll("%player%", player));
		
		int v = 0;
		for (Player p : Bukkit.getOnlinePlayers()) { //todo change to read from onlineVet list
			if (p.hasPermission("reservations.notify")) {
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BELL, 1.0F, 1.0F);
				Common.tell(p, pre + Config.playerConfirm.replaceAll("%player%", player));
				v++;
			}
		}
		if (sender instanceof Player) { //todo change to config based message.
			Common.tell(sender, pre + "There are " + v + " vets online right now.");
		}
	}
	
	@Subcommand("view|list")
	@Description("View current reservations")
	@CommandPermission("reservations.view")
	public void onView (CommandSender sender, @Default("all") String type) {
		if (CheckSender.isCommand(sender)) {
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
