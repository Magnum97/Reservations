package me.magnum.reservations;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.CommandReplacements;
import lombok.Getter;
import me.magnum.reservations.commands.Reservation;
import me.magnum.reservations.util.Config;
import me.magnum.reservations.util.SimpleConfig;
import org.bukkit.plugin.java.JavaPlugin;

import static me.magnum.reservations.util.Config.command;

public final class Reservations extends JavaPlugin {
	
	@Getter
	public static Reservations plugin;
	@Getter
	public static SimpleConfig cfg;
	private BukkitCommandManager commandManager;
	private CommandReplacements commands;
	
	@Override
	public void onEnable () {
		plugin = this;
		cfg = new SimpleConfig("config.yml");
		Config.init();
		commandManager = new BukkitCommandManager(this);
		commands = commandManager.getCommandReplacements();
		registerCommands();
	}
	
	private void registerCommands () {
		commandManager.enableUnstableAPI("help");
		commands.addReplacement("command", command);
		commandManager.registerCommand(new Reservation());
		
	}
	
	@Override
	public void onDisable () {
	
	}
}
