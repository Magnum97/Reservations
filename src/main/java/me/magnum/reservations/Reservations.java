package me.magnum.reservations;

import co.aikar.commands.BukkitCommandManager;
import com.earth2me.essentials.Essentials;
import de.leonhard.storage.Yaml;
import lombok.Getter;
import lombok.var;
import me.magnum.lib.Common;
import me.magnum.reservations.commands.Reservation;
import me.magnum.reservations.util.DataWorks;
import me.magnum.reservations.util.ReminderTask;
import me.magnum.reservations.util.VetListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

public final class Reservations extends JavaPlugin {

	@Getter
	public static Essentials ess;
	@Getter
	private static Reservations plugin;
	@Getter
	private static Yaml cfg;
	@Getter
	private static String pre;
	private final BukkitScheduler bs = Bukkit.getScheduler();
	@Getter
	private BukkitCommandManager commandManager;

	@Override
	public void onEnable () {
		plugin = this;
		Common.setInstance(plugin);
		var log = plugin.getLogger();
		log.info("Loading Config...");
		setupConfig();
		if (! hasEssentials()) {
			log.warning("Essentials not found. Disabling plugin");
			getServer().getPluginManager().disablePlugin(plugin);
			return;
		}
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

	private void setupConfig () {
		cfg = new Yaml("config.yml", plugin.getDataFolder().toString(), plugin.getResource("config.yml"));
		pre = cfg.getOrDefault("plugin-prefix", "&7[&dReservations&7] ");
		cfg.write();
		cfg.getOrSetDefault("plugin-prefix", "VetAssist");
		var dw = new DataWorks();
		dw.onLoad();
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

	private void setupEvents () {
		BukkitRunnable reminder = new ReminderTask();
		Bukkit.getPluginManager().registerEvents(new VetListener(), plugin);
		// Initial run 5 minutes after load, then once every x (default 60 seconds)
		reminder.runTaskTimerAsynchronously(plugin, 20 * 60 * 5, 20 * cfg.getOrSetDefault("reminder-delay", 60));
	}

	@SuppressWarnings ("deprecation")
	private void registerCommands () {
		commandManager = new BukkitCommandManager(plugin);
		var commands = commandManager.getCommandReplacements();
		commandManager.enableUnstableAPI("help");
		commands.addReplacement("command", cfg.getOrDefault("baseCommand", "apt"));
		commandManager.registerCommand(new Reservation());
	}

	@Override
	public void onDisable () {
		var dw = new DataWorks();
		bs.cancelTasks(plugin);
		dw.closeData();
	}
}
