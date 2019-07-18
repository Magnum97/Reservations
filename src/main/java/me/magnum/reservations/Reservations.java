package me.magnum.reservations;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.CommandReplacements;
import lombok.Getter;
import me.magnum.lib.Common;
import me.magnum.reservations.commands.Reservation;
import me.magnum.reservations.util.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Logger;

import static me.magnum.reservations.util.Config.*;
import static me.magnum.reservations.util.DataWorks.onlineVets;

public final class Reservations extends JavaPlugin {
	
	@Getter
	public static Reservations plugin;
	@Getter
	public static SimpleConfig cfg;
	@Getter public BukkitCommandManager commandManager;
	private CommandReplacements commands;
	public static Logger log;
	
	@SuppressWarnings("deprecation")
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
		Bukkit.getPluginManager().registerEvents(new VetListener(), plugin);
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask
				(plugin, new ReminderTask() ,(long) 20 * 300, (long) 20 * remindDelay );
	}
	
	@SuppressWarnings("deprecation")
	private void registerCommands () {
		commandManager.enableUnstableAPI("help");
		commands.addReplacement("command", command);
		commandManager.registerCommand(new Reservation());
		
	}
	
	@Override
	public void onDisable () {
		
		DataWorks.onlineVets.clear();
		DataWorks.clients.clear();
	}
}
