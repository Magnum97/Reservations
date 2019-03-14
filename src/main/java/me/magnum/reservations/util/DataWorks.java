package me.magnum.reservations.util;

import me.magnum.lib.CheckSender;
import me.magnum.reservations.Reservations;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import static me.magnum.reservations.util.Config.confirmAppt;
import static me.magnum.reservations.util.Config.pre;
import static org.bukkit.Bukkit.getOfflinePlayer;

public class DataWorks {
	
	public DataWorks () {
	}
	
	private static SimpleConfig data = new SimpleConfig("reservations.yml", false);
	public static LinkedHashMap <Integer, String> clients = new LinkedHashMap <>();
	private static int next;
	
	void onLoad () {
		Reservations.log.info("Getting next appointment");
		next = data.getInt("next-appointment", 1);
		try {
			Reservations.log.info("Loading waiting list...");
			for (String key : data.getConfigurationSection("waiting-list").getKeys(false)) {
				clients.put(Integer.parseInt(key), data.getString("waiting-list." + key));
			}
		}
		catch (NullPointerException e) {
			Reservations.log.warning("Could not load waiting list.");
			e.printStackTrace();
		}
	}
	
	
	public String make (String player) {
		String idString;
		String name;
		String result;
		OfflinePlayer p = getOfflinePlayer(player);
		if (p.hasPlayedBefore()) {
			idString = p.getUniqueId().toString();
			name = p.getName();
			// data.set("next-appointment", next);
			// data.set(idString + ".name", name);
			// data.set(idString + ".number", next);
			// for (int key : clients.keySet()) {
			// 	data.set("waiting-list" + key, clients.get(key));
			// }
			clients.put(next, idString);
			next++;
			data.set("next-appointment", next);
			data.write("waiting-list", clients);
			data.saveConfig();
			result = name + " " + confirmAppt;
			return result;
		}
		else {
			result = player + " has never logged in.";
			return result;
		}
	}
	
	public void view (CommandSender sender) {
		if (!CheckSender.isCommand(sender)) {
			HashMap <Integer, OfflinePlayer> list = new HashMap <>();
			// OfflinePlayer p;
			clients.forEach((i, s) -> list.put(i, getOfflinePlayer(UUID.fromString(s))));
			
			list.forEach(((i, p) ->
					sender.sendMessage(pre + "§6Number: §e" + i + " §6Client: §a" + p.getName() + "\n"))
			);
		}
		
	}
	
	public String clear (int key) {
		String name;
		String result;
		if (clients.containsKey(key)) {
			OfflinePlayer offlinePlayer = getOfflinePlayer(UUID.fromString(clients.get(key)));
			clients.remove(key);
			data.write("waiting-list", clients);
			result = offlinePlayer.getName() + " has been removed from the queue.";
			return result;
		}
		
		else {
			result = "A ticket with that number was not found";
			return result;
		}
		
	}
	
	public void closeData () {
		clients.clear();
	}
}
