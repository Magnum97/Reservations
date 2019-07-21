package me.magnum.reservations.util;

import com.google.gson.Gson;
import me.magnum.lib.CheckSender;
import me.magnum.lib.Common;
import me.magnum.reservations.Reservations;
import me.magnum.reservations.type.Appointment;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.*;
import java.util.*;

import static me.magnum.reservations.util.Config.*;
import static org.bukkit.Bukkit.getOfflinePlayer;

public class DataWorks {
	
	private static Reservations plugin = Reservations.getPlugin();
	private static final SimpleConfig data = new SimpleConfig("reservations.yml", false);
	
	private File aptBook = new File(plugin.getDataFolder() + File.separator + "appointments.json");
	public static Map <Integer, String> clients = new TreeMap <>();
	public static List <Player> onlineVets = new ArrayList <>();
	private static Map <LocalDateTime, Appointment> appointmentMap = new TreeMap <>();
	
	// public static List <HashMap <OfflinePlayer, Appointment>> appt = new LinkedList <>();
	// public static List <Appointment> appointmentMap = new LinkedList <>();
	
	private static int next;
	Gson gson = new Gson();
	
	public DataWorks () {
	}
	
	void onLoad () {
		Common.log("Getting next appointment");
		next = data.getInt("next-appointment", 1);
		try {
			Common.log("Loading waiting list...");
			for (String key : data.getConfigurationSection("waiting-list").getKeys(false)) {
				clients.put(Integer.parseInt(key), data.getString("waiting-list." + key));
			}
		}
		catch (NullPointerException e) {
			Common.log("Could not load waiting list.");
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	private OfflinePlayer getPlayer (String player) {
		OfflinePlayer offlinePlayer = getOfflinePlayer(player);
		if (offlinePlayer.hasPlayedBefore()) {
			return offlinePlayer;
		}
		return null;
	}
	
	private boolean saveApt (String jsonAppt) {
		try {   // this is for monitoring runtime Exception within the block
			BufferedWriter writer = new BufferedWriter(new FileWriter(aptBook));
			
			String content = "This is the content to write into file"; // content to write into the file
			
			writer.write(jsonAppt);
			
			// File file = new File("C:/Users/Geroge/SkyDrive/Documents/inputFile.txt"); // here file not created here
			
			// if file doesnt exists, then create it
			if (!aptBook.exists()) {   // checks whether the file is Exist or not
				aptBook.createNewFile();   // here if file not exist new file created
			}
			
			FileWriter fw = new FileWriter(aptBook.getAbsoluteFile(), true); // creating fileWriter object with the file
			BufferedWriter bw = new BufferedWriter(fw); // creating bufferWriter which is used to write the content into the file
			bw.write(jsonAppt); // write method is used to write the given content into the file
			bw.close(); // Closes the stream, flushing it first. Once the stream has been closed, further write() or flush() invocations will cause an IOException to be thrown. Closing a previously closed stream has no effect.
			
			System.out.println("Done");
			
		}
		catch (IOException e) { // if any exception occurs it will catch
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public String makeAppt (String player, String time) {
		String reason = "";
		return makeAppt(player, time, reason);
	}
	
	public String makeAppt (String player, String time, String reason) {
		String result;
		OfflinePlayer offlinePlayer = getPlayer(player);
		if ((offlinePlayer == null) || !offlinePlayer.hasPlayedBefore()) {
			result = player + " has never logged in to this server."; //todo move to config
			return result;
		}
		LocalDateTime ldt = getTime(time);
		Appointment appointment = new Appointment(ldt, offlinePlayer.getUniqueId().toString(), reason);
		result = pre + "Appointment created";
		String jsonString = gson.toJson(appointment);
		System.out.println(jsonString); //todo remove debug msg
		saveApt(jsonString);
		
		appointmentMap.put(ldt, appointment);
		
		return result;
	}
	
	public void listAppointments (CommandSender sender) {
		if (!CheckSender.isCommand(sender)) {
			if (appointmentMap.isEmpty()) {
				Common.tell(sender, pre + "No appts");
			}
		}
		appointmentMap.forEach((t, i) ->
				                       Common.tell(sender, pre + t.toString() + " " +
						                       getOfflinePlayer(UUID.fromString(i.getPlayerId())).getName()));
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
	
	private LocalDateTime parseDateTime (String date, String time) {
		LocalDateTime ldt = null;
		try {
			LocalDate parseDate = LocalDate.parse(date);
			LocalTime parseTime = LocalTime.parse(time);
			ldt = LocalDateTime.of(parseDate, parseTime);
		}
		catch (DateTimeException de) {
			de.printStackTrace();
			Common.log(pre + "§3Invalid date");
		}
		return ldt;
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
			clients.forEach((n, o) ->
					                Common.tell(sender, pre + format
							                .replaceAll("#", n.toString())
							                .replaceAll("%player%", getOfflinePlayer(UUID.fromString(o)).getName())));
			
		}
		
	}
	
	public String clear (int key) {
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
	
	public void wipe (CommandSender sender) {
		if (clients.size() > 1) {
			HashMap <Integer, String> res = new HashMap <Integer, String>(clients);
			for (int i : res.keySet()) {
				Common.tell(sender, clear(i));
			}
		}
		else {
			Common.tell(sender, noAppt);
		}
	}
	
	/**
	 * Add player to list of online vets
	 *
	 * @param player Player to add to the list
	 *               of online vets.
	 */
	public void addVet (Player player) {
		onlineVets.add(player);
	}
	
	/**
	 * Remove a vet from the list to be notified
	 * of waiting appointments.
	 *
	 * @param player Player to remove from the list.
	 */
	public void removeVet (Player player) {
		onlineVets.remove(player);
	}
	
	/**
	 * Get a LocalDateTime of next occurrence of specified
	 * time
	 *
	 * @param time String of time in format HH:mm or hh:mma
	 * @return
	 */
	public LocalDateTime getTime (String time) { //todo remove debugging messages
		// DateTimeFormatter formata = DateTimeFormatter.ofPattern("HH:mm");
		// DateTimeFormatter formatb = DateTimeFormatter.ofPattern("hh:mm");
		LocalTime midnight = LocalTime.MIDNIGHT;
		LocalDate today = LocalDate.now(ZoneId.of("US/Eastern"));
		
		LocalDateTime lastMidnight = LocalDateTime.of(today, midnight);
		LocalDateTime lt = LocalDateTime.now();
		System.out.println(lt);
		// System.out.println("lt =" + lt.format(formata));
		// System.out.println("lt =" + lt.format(formatb));
		
		String stringHours = time.split("[:]")[0];
		System.out.println(stringHours);
		int hours = Integer.valueOf(time.split("[:]")[0]);
		if (time.contains("p")) {
			hours += 12;
			System.out.println(hours);
			System.out.println(time);
			time = time.split("[:]")[1].split("[p]")[0];
			System.out.println(time);
		}
		else {
			time = time.split("[:]")[1];
		}
		System.out.println(time);
		int minutes = Integer.valueOf(time);
		
		LocalDateTime tt = lastMidnight.plusHours(hours).plusMinutes(minutes);
		System.out.println("tt =" + tt);
		
		if (tt.isBefore(lt)) {
			tt = tt.plusDays(1);
			System.out.println("New tt = " + tt);
		}
		else {
			System.out.println("Time is already in the future.");
		}
		return tt;
	}
	
	public void closeData () { //todo Save Appointments to config file
		clients.clear();
	}
}
