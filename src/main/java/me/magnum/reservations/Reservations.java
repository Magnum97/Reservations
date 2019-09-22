package me.magnum.reservations;

import co.aikar.commands.BukkitCommandManager;
import com.earth2me.essentials.Essentials;
import lombok.Getter;
import lombok.var;
import me.magnum.lib.Common;
import me.magnum.lib.SimpleConfig;
import me.magnum.reservations.commands.Reservation;
import me.magnum.reservations.util.Config;
import me.magnum.reservations.util.DataWorks;
import me.magnum.reservations.util.ReminderTask;
import me.magnum.reservations.util.VetListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import static me.magnum.reservations.util.Config.command;

public final class Reservations extends JavaPlugin {

	@Getter
	public static Essentials ess;
	@Getter
	private static Reservations plugin;
	@Getter
	private BukkitCommandManager commandManager;
	private BukkitScheduler bs = Bukkit.getScheduler();

	@Override
	public void onEnable () {
		plugin = this;
		var log = plugin.getLogger();
		Common.setInstance(plugin);
		if (!hasEssentials()) {
			log.warning("Essentials not found. Disabling plugin");
			plugin.onDisable();
			return;
		}
		log.info("Loading Config...");
		var config = new SimpleConfig("config.yml", plugin);
		Config.init();
		log.info("Initializing command manager...");
		registerCommands();
		log.info("Registering command...");
		log.info("Registering event listeners...");
		setupEvents();
		if (plugin.isEnabled()) {
			log.info("Plugin enabled.");
		}
		else {
			log.severe("Something went wrong. Plugin Disabled");
		}
	}

	private boolean hasEssentials () {
		if (getServer().getPluginManager().getPlugin("Essentials") != null) {
			ess = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
			return true;
		}
		else {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	private void setupEvents () {
		var reminder = new ReminderTask();
		Bukkit.getPluginManager().registerEvents(new VetListener(), plugin);
		bs.runTaskLater(plugin, reminder, 20 * 10);
		bs.scheduleSyncRepeatingTask(plugin, reminder, 20 * 300, 20 * Config.remindDelay);

	}

	@SuppressWarnings("deprecation")
	private void registerCommands () {
		commandManager = new BukkitCommandManager(plugin);
		var commands = commandManager.getCommandReplacements();
		commandManager.enableUnstableAPI("help");
		commands.addReplacement("command", command);
		commandManager.registerCommand(new Reservation());
	}

	@Override
	public void onDisable () {
		var dw = new DataWorks();
		bs.cancelAllTasks();
		dw.closeData();
	}
}
