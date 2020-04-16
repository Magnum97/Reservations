package me.magnum.reservations;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.CommandReplacements;
import lombok.Getter;
import me.magnum.reservations.commands.Reservation;
import me.magnum.reservations.util.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.mineacademy.fo.Common;

public final class Reservations extends JavaPlugin {

	@Getter
	public static Reservations plugin;
	@Getter
	public BukkitCommandManager commandManager;
	private CommandReplacements commands;
	private BukkitScheduler bs = Bukkit.getScheduler();
	@Getter
	private SimpleConfigManager manager;
	@Getter
	private SimpleConfig cfg;

	@SuppressWarnings ("deprecation")
	@Override
	public void onEnable () {
		plugin = this;
		ReminderTask reminder = new ReminderTask();
		Common.log("Loading Config...");
		manager = new SimpleConfigManager(plugin);
//		cfg = new Config();
		cfg = manager.getNewConfig("config.yml",true);
		Common.log("Initializing command manager...");
		commandManager = new BukkitCommandManager(this);
		commands = commandManager.getCommandReplacements();
		registerCommands();
		Common.log("Registering commands");
		Bukkit.getPluginManager().registerEvents(new VetListener(), plugin);
		bs.runTaskLater(plugin, reminder, 20 * 10);
		bs.scheduleSyncRepeatingTask(plugin, reminder, 20 * 300, 20 * cfg.getInt("reminder-delay"));
	}

	@SuppressWarnings ("deprecation")
	private void registerCommands () {
		commandManager.enableUnstableAPI("help");
		commands.addReplacement("command", cfg.getString("baseCommand"));
		commandManager.registerCommand(new Reservation());
	}

	@Override
	public void onDisable () {
		DataWorks dw = new DataWorks();
		bs.cancelAllTasks();
		dw.closeData();
	}
}
