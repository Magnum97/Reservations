package me.magnum.reservations.util;

public class Config extends SimpleConfig {
	
	public static int next;
	public static String pre;
	public static String confirmAppt;
	public static String command;
	
	public Config (String fileName) {
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
		// Set stuff here
		pre = getString("plugin-prefix").replace("&", "§");
		command = getString("baseCommand");
		confirmAppt = getString("appointment-confirm");
	}
	
	public static void init () {
		new Config("config.yml").onLoad();
	}
}
