package me.magnum.reservations.util;

import me.magnum.lib.CheckSender;
import me.magnum.lib.Common;
import me.magnum.reservations.Reservations;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import static me.magnum.reservations.util.Config.*;
import static org.bukkit.Bukkit.getOfflinePlayer;

public class DataWorks {
	
	public DataWorks () {
	}
	
	private static final SimpleConfig data = new SimpleConfig("reservations.yml", false);
	public static LinkedHashMap <Integer, String> clients = new LinkedHashMap <>(99,.75f,false);
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
	
	@SuppressWarnings("deprecation")
	public String make (String player) {
		String idString;
		String result;
		OfflinePlayer p = getOfflinePlayer(player);
		if (p.hasPlayedBefore()) {
			idString = p.getUniqueId().toString();
			clients.put(next, idString);
			next++;
			if (next > 99) {
				next = 1;
			}
			data.set("next-appointment", next);
			data.write("waiting-list", clients);
			data.saveConfig();
			result = confirmAppt.replaceAll("%player%", p.getName());
			return result;
		}
		else {
			result = player + " has not logged in before.";
			return result;
		}
	}
	@SuppressWarnings("deprecation")
	public boolean check (String player) {
		OfflinePlayer p = getOfflinePlayer(player);
		return clients.containsValue(p.getUniqueId().toString());
	}
	
	public void view (CommandSender sender) {
		if (!CheckSender.isCommand(sender)) {
			if (clients.size() < 1) {
				Common.tell(sender, pre + Config.noAppt);
			}
			HashMap <Integer, OfflinePlayer> list = new HashMap <>();
			// OfflinePlayer p;
			clients.forEach((i, s) -> list.put(i, getOfflinePlayer(UUID.fromString(s))));
			
			list.forEach(((i, p) ->
					Common.tell(sender, pre + format.
							replaceAll("#", i.toString()).replaceAll("%player%", p.getName())))
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
