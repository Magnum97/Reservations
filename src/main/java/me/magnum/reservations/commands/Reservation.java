package me.magnum.reservations.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import me.magnum.lib.CheckSender;
import me.magnum.reservations.util.DataWorks;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashMap;

import static me.magnum.reservations.util.Config.pre;

@CommandAlias("%command")
public class Reservation extends BaseCommand {
	
	
	public Reservation () {
	}
	
	
	@Subcommand("make")
	@Description("Get a number")
	public void onMake (CommandSender sender, String player) {
		DataWorks dw = new DataWorks();
		String result = dw.make(player);
		sender.sendMessage(pre + result);
	}
	
	@Subcommand("view")
	@Description("View current reservations")
	@CommandPermission("appointment.view")
	public void onView (CommandSender sender) {
		if (CheckSender.isCommand(sender)) {
			return;
		}
		DataWorks dw = new DataWorks();
		LinkedHashMap <Integer, String> result;
		dw.view(sender);
	}
	
	
	@Subcommand("clear")
	@Description("Clear a reservation from the list")
	@CommandPermission("appointment.clear")
	public void onClear (CommandSender sender, int key) {
		DataWorks dw = new DataWorks();
		String result = dw.clear(key);
		sender.sendMessage(pre + result);
	}
	
	@HelpCommand
	public void onHelp (CommandSender sender, CommandHelp help) {
		help.showHelp();
	}
}
