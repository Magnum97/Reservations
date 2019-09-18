package me.magnum.reservations.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.earth2me.essentials.User;
import lombok.var;
import me.magnum.lib.CheckSender;
import me.magnum.lib.Common;
import me.magnum.reservations.Reservations;
import me.magnum.reservations.util.Config;
import me.magnum.reservations.util.DataWorks;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static me.magnum.reservations.util.Config.*;
import static me.magnum.reservations.util.DataWorks.onlineVets;

@CommandAlias("%command")
public class Reservation extends BaseCommand {


	public Reservation () {
	}

	@Subcommand("make|call")
	@Description("Make a reservation and get a number")
	@CommandCompletion("@players")
	@CommandPermission("reservations.make.self")
	public void onMake (CommandSender sender, @Default("") String player, @Default("") String time, @Default("") String reason) throws IllegalAccessException {
		if (CheckSender.isCommand(sender)) {
			return;
		}
		if (player.equals("")) {
			if ((CheckSender.isConsole(sender))) {
				Common.tell(sender, pre + "I'm sorry console, you can't make an appointment for yourself.");
				return;
			}
			else {
				player = sender.getName();
			}
		}
		DataWorks dw = new DataWorks();
		String result;
		if (sender instanceof Player) {
			if ((!(sender.hasPermission("reservations.make.others"))) && (!(sender.getName().equalsIgnoreCase(player)))) {
				Common.tell(sender, pre + Config.noMakeOther);
				return;
			}
		}
		if (time.length() > 0) {
			if (!time.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9](\\b|[a|p])")) {
				// getCurrentCommandManager().generateCommandHelp("make");
				Common.tell(sender, pre + time + "&e is not a valid time."
						, pre + "&bPlease format time:&e HH:mm &bYou can use 24 hour time or 12 hour with &ea&7/&ep&b"
						, pre + "&bFor example &f15:30 &b| &f3:30p &bor &f03:30&b | &f3:30a");
				return;
			}
			else {
				if (dw.hasApt(player)) {
					Common.tell(sender, pre + aptUpdate.replace("%player%", player));
					dw.updateApt(dw.getApt(player), time, reason);
					return;
				}
				result = dw.makeAppt(player, time, reason);
				Common.tell(sender, pre + result);
				return;
			}
		}
		if (sender instanceof Player) {
			((Player) sender).playSound(((Player) sender).getLocation(), Sound.BLOCK_NOTE_BELL, 1.0F, 1.0F);
		}
		if (dw.checkNumber(player)) {
			Common.tell(sender, pre + Config.hasAppt);
			return;
		}
		result = dw.takeNumber(player, reason);
		if (result.contains(sender.getName())) {
			result = result.replace(sender.getName(), "You");
		}
		Common.tell(sender, pre + result);
		Common.setInstance(Reservations.getPlugin());
		Common.log(Config.logConfirm.replaceAll("%player%", player));
		ArrayList <String> vets = new ArrayList <>();
		for (Player vet : onlineVets) {
			vets.add(vet.getName());
			vet.playSound(vet.getLocation(), Sound.BLOCK_NOTE_BELL, 1.0F, 1.0F);
			Common.tell(vet, pre + Config.playerConfirm.replaceAll("%player%", player));
		}
		if (sender instanceof Player) { //todo change to config based message.
			Common.tell(sender, pre + "There are " + onlineVets.size() + " vets online right now:",
			            "&9 " + vets);
		}
	}

	@Subcommand("view|list")
	@Description("View current reservations")
	@CommandPermission("reservations.view")
	public void onView (CommandSender sender, @Default("all") String type) {
		if (CheckSender.isCommand(sender)) {
			return;
		}
		if (type.equalsIgnoreCase("help")) {
			Common.tell(sender,
			            pre + "Command useage: &e/va view [all | waiting | apt]",
			            pre + "To list all, only waiting, or only scheduled appointments.");
			return;
		}
		DataWorks dw = new DataWorks();
		if ((type.equalsIgnoreCase("waiting") ||
				type.equalsIgnoreCase("all"))) {
			dw.showWaiting(sender);
		}
		if (type.equalsIgnoreCase("apt") ||
				type.equalsIgnoreCase("all")) {
			dw.showAppointments(sender);
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

	@Subcommand("cancel")
	@Description("Cancel an appointment")
	@CommandPermission("reservations.cancel.self")
	public void onCancel (CommandSender sender, @Default("") String player) throws IllegalAccessException {
		if (CheckSender.isCommand(sender)) {
			return;
		}
		DataWorks dw = new DataWorks();
		String result;
		if (player.equals("")) {
			if (CheckSender.isConsole(sender)) {
				Common.tell(sender, pre + "There are no appointments for console.");
				return;
			}
			else {
				player = sender.getName();
			}
		}
		if (CheckSender.isPlayer(sender)) {
			if ((!(sender.hasPermission("reservations.make.others"))) && (!(sender.getName().equalsIgnoreCase(player)))) {
				Common.tell(sender, pre + Config.noCancelOther);
				return;
			}
		}

		if (dw.hasApt(player)) {
			dw.cancelApt(dw.getApt(player));
			Common.tell(sender, pre + canceled.replace("%player%", player));
		}
		else {
			Common.tell(sender, pre + hasNoApt.replace("%player%", player));
		}
	}

	@Subcommand("wipe")
	@Description("Wipe the list clean baby!")
	@CommandPermission("reservations.clear.all")
	public void onWipe (CommandSender sender, @Default("") String confirm) {
		// if (confirm == null) {
		// 	confirm = "";
		// }
		if (confirm.equalsIgnoreCase("confirm")) {
			DataWorks dw = new DataWorks();
			dw.wipe(sender);
			Common.tell(sender, pre+"&eThe waiting list has been wiped.");
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
