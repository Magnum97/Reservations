package me.magnum.reservations.util;

import me.magnum.lib.Common;
import me.magnum.reservations.Reservations;

@SuppressWarnings("SpellCheckingInspection")
public class Config extends SimpleConfig {
	
	public static int next;
	public static String pre;
	public static String confirmAppt;
	public static String command;
	public static String playerConfirm;
	public static String logConfirm;
	public static String noAppt;
	public static String noMakeOther;
	public static String hasAppt;
	public static String format;
	public static String waiting;
	public static int remindDelay;
	
	private Config (String fileName) {
		super(fileName);
		setHeader(new String[] {
				"--------------------------------------------------------",
				" Your configuration file got updated automatically!",
				" ",
				" Unfortunately, due to how Bukkit saves .yml files,",
				" all comments in your file were lost. Please open",
				" " + fileName + " from jar to browse the default values.",
				"--------------------------------------------------------"
		});
	}
	
	private void onLoad () {
		Common.setInstance(Reservations.getPlugin());
		// Set stuff here
		DataWorks dw = new DataWorks();
		dw.onLoad();
		pre = getString("plugin-prefix").replace("&", "§");
		command = getString("baseCommand");
		confirmAppt = getString("messages.appointment-confirm");
		playerConfirm = getString("messages.player-confirm");
		logConfirm = getString("messages.log-confirm");
		noAppt = getString("messages.no-appts");
		noMakeOther = getString("messages.no-permission");
		hasAppt = getString("messages.has-appt");
		format = getString("list-format");
		waiting = getString("messages.waiting");
		remindDelay = getInt("reminder-delay");
	}
	
	public static void init () {
		new Config("config.yml").onLoad();
	}
}
