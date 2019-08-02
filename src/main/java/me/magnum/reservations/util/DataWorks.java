package me.magnum.reservations.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import me.magnum.lib.CheckSender;
import me.magnum.lib.Common;
import me.magnum.lib.SimpleConfig;
import me.magnum.reservations.Reservations;
import me.magnum.reservations.type.Appointment;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.*;
import java.time.*;
import java.util.*;

import static me.magnum.reservations.util.Config.*;
import static org.bukkit.Bukkit.getOfflinePlayer;

public class DataWorks {
	
	private static Reservations plugin = Reservations.getPlugin();
	private static final SimpleConfig data = new SimpleConfig("reservations.yml", Reservations.plugin,false);
	private File aptBook = new File(plugin.getDataFolder() + File.separator + "appointments.json");
	static List <Player> onlineVets = new ArrayList <>();
	static Map <Integer, String> walkIns = new TreeMap <>();
	
	@Getter
	public static List <Appointment> appointmentList = new ArrayList <>();
	
	private static int next;
	private Gson gson = new Gson().newBuilder().setPrettyPrinting().create();
	
	public DataWorks () {
	}
	
	void onLoad () {
		Common.log("Getting next appointment");
		next = data.getInt("next-appointment", 1);
		Common.log("Loading waiting list...");
		try {
			for (String key : data.getConfigurationSection("waiting-list").getKeys(false)) {
				walkIns.put(Integer.parseInt(key), data.getString("waiting-list." + key));
			}
			
		}
		catch (NullPointerException e) {
			e.printStackTrace();
			Common.log("Could not load waiting list.");
		}
		Common.log("Loading Appointments..");
		try {
			Reader reader = new FileReader(aptBook);
			Appointment[] apli = gson.fromJson(reader, Appointment[].class);
			appointmentList = new ArrayList <>(Arrays.asList(apli));
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			Common.log("Could not load appointments.");
		}
		catch (JsonSyntaxException jse) {
			jse.printStackTrace();
			Common.log("&cData file was wrongly formatted.", "Could not load appointments.");
		}
		Common.log("Appt loaded");
	}
	
	@SuppressWarnings("deprecation")
	private OfflinePlayer getPlayer (String player) {
		OfflinePlayer offlinePlayer = getOfflinePlayer(player);
		if (offlinePlayer.hasPlayedBefore()) {
			return offlinePlayer;
		}
		return null;
	}
	
	public void saveAll () {
		
		timedClear();
		String jsonAppt = gson.toJson(appointmentList);
		try {
			if (!aptBook.exists()) {   // checks whether the file is Exist or not
				aptBook.createNewFile();   // here if file not exist new file created
			}
			
			FileWriter fw = new FileWriter(aptBook.getAbsoluteFile()); // creating fileWriter object with the file
			BufferedWriter bw = new BufferedWriter(fw); // creating bufferWriter which is used to write the content into the file
			bw.write(jsonAppt);
			Common.log(pre + "Appointments saved");
			bw.close(); // Closes the stream, flushing it first. Once the stream has been closed, further write() or flush() invocations will cause an IOException to be thrown. Closing a previously closed stream has no effect.
		}
		catch (IOException e) { // if any exception occurs it will catch
			e.printStackTrace();
		}
	}
	
	private void saveApt (String jsonAppt) {
		try {
			// if file doesnt exists, then create it
			if (!aptBook.exists()) {   // checks whether the file is Exist or not
				aptBook.createNewFile();   // here if file not exist new file created
			}
			
			FileWriter fw = new FileWriter(aptBook.getAbsoluteFile(), true); // creating fileWriter object with the file
			BufferedWriter bw = new BufferedWriter(fw); // creating bufferWriter which is used to write the content into the file
			bw.write(jsonAppt); // write method is used to write the given content into the file
			bw.close(); // Closes the stream, flushing it first. Once the stream has been closed, further write() or flush() invocations will cause an IOException to be thrown. Closing a previously closed stream has no effect.
			Common.log(pre + "Appointment created");
			
		}
		catch (IOException e) { // if any exception occurs it will catch
			e.printStackTrace();
		}
		
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
		result = "Appointment created";
		addAppointment(appointment);
		return result;
	}
	
	public void addAppointment (Appointment appointment) {
		appointmentList.add(appointment);
	}
	
	@SuppressWarnings("deprecation")
	public void listAppointments (CommandSender sender) {
		if (appointmentList.isEmpty()) {
			Common.tell(sender, pre + "No appts"); // todo verbose
			return;
		}
		String pattern = "E HH:mm"; // todo add to config
		Collections.sort(appointmentList, Appointment::compareTo);
		
		for (Appointment a : appointmentList) {
			String uuid = a.getPlayerId();
			String player = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
			Common.tell(sender, pre + "Time: " + a.getTime() + " Name: " + player);
		}
	}
	
	@SuppressWarnings("deprecation")
	public String make (String player) {
		String idString;
		String result;
		OfflinePlayer p = getOfflinePlayer(player);
		if (p.hasPlayedBefore()) {
			idString = p.getUniqueId().toString();
			walkIns.put(next, idString);
			next++;
			if (next > 99) {
				next = 1;
			}
			data.set("next-appointment", next);
			data.write("waiting-list", walkIns);
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
	public boolean checkNumber (String player) {
		OfflinePlayer p = getOfflinePlayer(player);
		String playerId = p.getUniqueId().toString();
		return walkIns.containsValue(playerId);
	}
	
	@SuppressWarnings("deprecation")
	public boolean hasApt (String player) {
		OfflinePlayer p = getOfflinePlayer(player);
		String playerId = p.getUniqueId().toString();
		boolean onList = false;
		for (Appointment a : appointmentList) {
			onList = a.getPlayerId().equals(playerId);
		}
		return onList;
	}
	
	public void view (CommandSender sender) {
		if (!CheckSender.isCommand(sender)) {
			if (walkIns.size() < 1) {
				Common.tell(sender, pre + Config.noAppt);
			}
			walkIns.forEach((n, o) -> {
				Common.tell(sender, pre + format
						.replaceAll("#", n.toString())
						.replaceAll("%player%", getOfflinePlayer(UUID.fromString(o)).getName()));
			});
			
		}
		
	}
	
	public String clear (int key) {
		String result;
		if (walkIns.containsKey(key)) {
			OfflinePlayer offlinePlayer = getOfflinePlayer(UUID.fromString(walkIns.get(key)));
			walkIns.remove(key);
			data.write("waiting-list", walkIns);
			result = offlinePlayer.getName() + " has been removed from the queue.";
			return result;
		}
		
		else {
			result = "A ticket with that number was not found";
			return result;
		}
		
	}
	
	public void wipe (CommandSender sender) {
		if (walkIns.size() > 1) {
			HashMap <Integer, String> res = new HashMap <>(walkIns);
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
	private LocalDateTime getTime (String time) {
		LocalTime midnight = LocalTime.MIDNIGHT;
		LocalDate today = LocalDate.now(ZoneId.of("US/Eastern"));
		LocalDateTime lastMidnight = LocalDateTime.of(today, midnight);
		LocalDateTime lt = LocalDateTime.now();
		String stringHours = time.split("[:]")[0];
		int hours = Integer.valueOf(stringHours);
		if (time.matches(".{4,5}[p]")) { // todo fix detection os 12:00p as midnight
			hours += 12;
			time = time.split("[:]")[1].split("[p]")[0];
		}
		else {
			time = time.split("[:]")[1];
		}
		time = time.replaceFirst("\\d\\d([a|p])", "");
		int minutes = Integer.valueOf(time);
		LocalDateTime tt = lastMidnight.plusHours(hours).plusMinutes(minutes);
		
		if (tt.isBefore(lt)) {
			tt = tt.plusDays(1);
		}
		return tt;
	}
	
	@SuppressWarnings("deprecation")
	public Appointment getApt (String player) throws IllegalAccessException {
		String pid = getOfflinePlayer(player).getUniqueId().toString();
		int i = 0;
		while (i < appointmentList.size()) {
			if (appointmentList.get(i).getPlayerId().equalsIgnoreCase(pid)) {
				return appointmentList.get(i);
			}
			i++;
		}
		return new Appointment();
	}
	
	public void cancelApt (Appointment appointment) {
		appointmentList.remove(appointment);
	}
	
	public void updateApt (Appointment appointment, String newTime, String reason) {
		appointment.setTime(getTime(newTime));
		appointment.setReason(reason);
	}
	
	public void clearCanceled () {
		Iterator it = appointmentList.iterator();
		Appointment appointment;
		while (it.hasNext()) {
			appointment = (Appointment) it.next();
			if (appointment.isCanceled()) {
				appointmentList.remove(appointment);
			}
		}
	}
	
	public void clearAppt (Appointment appointment) {
		appointment.setCanceled(true);
		appointmentList.remove(appointment);
	}
	
	private void timedClear () {
		if (appointmentList.isEmpty()) {
			Common.log("App list empty");
		}
		List <Appointment> toRemove = new ArrayList <>();
		for (Appointment a : appointmentList) {
			LocalDateTime lt = LocalDateTime.now();
			if (a.getTime().isBefore(lt.minusMinutes(30))) {
				toRemove.add(a);
			}
		}
		toRemove.forEach(a -> appointmentList.remove(a));
	}
	
	public void closeData () { //todo Save Appointments to config file
		saveAll();
		walkIns.clear();
		onlineVets.clear();
	}
}
