package me.magnum.reservations;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.CommandReplacements;
import lombok.Getter;
import me.magnum.reservations.commands.Reservation;
import me.magnum.reservations.util.Config;
import me.magnum.reservations.util.DataWorks;
import me.magnum.reservations.util.SimpleConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

import static me.magnum.reservations.util.Config.command;

public final class Reservations extends JavaPlugin {
	
	@Getter
	public static Reservations plugin;
	@Getter
	public static SimpleConfig cfg;
	private BukkitCommandManager commandManager;
	private CommandReplacements commands;
	public static Logger log;
	
	@Override
	public void onEnable () {
		plugin = this;
		log = Bukkit.getLogger();
		cfg = new SimpleConfig("config.yml");
		log.info("Loading Config...");
		Config.init();
		log.info("Initializing command manager...");
		commandManager = new BukkitCommandManager(this);
		commands = commandManager.getCommandReplacements();
		registerCommands();
		log.info("Registering commands");
	}
	
	private void registerCommands () {
		commandManager.enableUnstableAPI("help");
		commands.addReplacement("command", command);
		commandManager.registerCommand(new Reservation());
		
	}
	
	@Override
	public void onDisable () {
		DataWorks.clients.clear();
	}
}
