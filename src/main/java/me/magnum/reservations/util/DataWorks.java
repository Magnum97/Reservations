package me.magnum.reservations.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import me.magnum.lib.Common;
import me.magnum.reservations.Reservations;
import me.magnum.reservations.type.Appointment;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static me.magnum.reservations.Reservations.CFG;
import static me.magnum.reservations.util.Config.*;
import static org.bukkit.Bukkit.getOfflinePlayer;

public class DataWorks {

	static List <Player> onlineVets = new ArrayList <>();
	static Map <Integer, Appointment> walkIns = new TreeMap <>();
	private static List <Appointment> appointmentList = new ArrayList <>();
	private static List <Appointment> dropIn = new ArrayList <>();
	private static List <OfflinePlayer> playerList = new ArrayList <>();
	private static Reservations plugin = Reservations.getPlugin();
	private static int next;
	private File aptBook = new File(plugin.getDataFolder() + File.separator + "appointments.json");
	private File waiting = new File(plugin.getDataFolder() + File.separator + "walkins.json");
	private Gson gson = new Gson().newBuilder().setPrettyPrinting().create();

	public DataWorks () {
	}

	void onLoad () {
		Common.log("Getting next appointment");
		Common.log("Loading waiting list...");
		try {
			Reader reader = new FileReader(waiting);
			Appointment[] waitingArray = gson.fromJson(reader, Appointment[].class);
			dropIn = new ArrayList <>(Arrays.asList(waitingArray));
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			Common.log("Could not load waiting list.");
		}
		catch (JsonSyntaxException jse) {
			jse.printStackTrace();
			Common.log("&cData file was corrupt or wrongly formatted.", "Could not load waiting list.");
		}
		Common.log("Waiting-list loaded",
		           "Loading Appointments..");
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
		setNext();
	}

	private void setNext () {
		int max = dropIn.get(0).getNumber();
		for (Appointment appointment : dropIn) {
			if (appointment.getNumber() > max) {
				max = appointment.getNumber();
			}
		}
		next = max + 1;
	}

	@SuppressWarnings("deprecation")
	private OfflinePlayer getPlayer (String player) {
		OfflinePlayer offlinePlayer = getOfflinePlayer(player);
		if (offlinePlayer.hasPlayedBefore()) {
			return offlinePlayer;
		}
		return null;
	}

	private void saveAll () {
		timedClear();
		String jsonAppt = gson.toJson(appointmentList);
		String jsonWaiting = gson.toJson(dropIn);
		CFG.saveConfig();
		try {
			FileWriter aptFW = new FileWriter(aptBook.getAbsoluteFile()); // creating fileWriter object with the file
			BufferedWriter bw = new BufferedWriter(aptFW); // creating bufferWriter which is used to write the content into the file
			if (!aptBook.exists()) {   // checks whether the file is Exist or not
				aptBook.createNewFile();   // here if file not exist new file created
			}

			bw.write(jsonAppt);
			Common.log(pre + "Appointments saved");
			bw.close(); // Closes the stream, flushing it first. Once the stream has been closed, further write() or flush() invocations will cause an IOException to be thrown. Closing a previously closed stream has no effect.
		}
		catch (IOException e) { // if any exception occurs it will catch
			e.printStackTrace();
		}
		try {
			if (!waiting.exists()) {
				waiting.createNewFile();
			}
			FileWriter waitingFW = new FileWriter(waiting.getAbsoluteFile());
			BufferedWriter bwl = new BufferedWriter(waitingFW);
			bwl.write(jsonWaiting);
			Common.log("Drop in list saved");
			bwl.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
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
	public void showAppointments (CommandSender sender) {
		if (appointmentList.isEmpty()) {
			Common.tell(sender, pre + "There are currently no appointments."); // todo verbose and config
			return;
		}
		String pattern = "E, HH:mm"; // todo add to config
		appointmentList.sort(Appointment::compareTo);
		for (Appointment a : appointmentList) {
			String uuid = a.getPlayerId();
			String player = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
			Common.tell(sender, pre + "Time: " + a.getTime() + " Name: " + player);
		}
	}


	public String takeNumber (String player) {
		return takeNumber(player, "");
	}

	/**
	 * Add player to queue and
	 * assigns a number.
	 *
	 * @param player the player
	 * @param reason the reason
	 * @return the string
	 */
	@SuppressWarnings("deprecation")
	public String takeNumber (String player, String reason) {
		String playerId;
		String result;
		OfflinePlayer p = getOfflinePlayer(player);
		if (p.hasPlayedBefore()) {
			playerId = p.getUniqueId().toString();
			Appointment walkIn = new Appointment(LocalDateTime.now(Clock.systemDefaultZone()), playerId, reason, next);
			dropIn.add(walkIn);
			walkIns.put(next, walkIn);
			next++;
			result = confirmAppt.replace("%player%", p.getName());
			return result;
		}
		else {
			result = player + " has not logged into this server before.";
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

	/**
	 * Check if player has a number already.
	 *
	 * @param player the player
	 * @return the boolean
	 */
	@SuppressWarnings("deprecation")
	public boolean checkNumber (String player) {
		OfflinePlayer p = getOfflinePlayer(player);
		String playerId = p.getUniqueId().toString();
		return getByUUID(playerId).isPresent();
	}

	/**
	 * Check if player has an appointment.
	 *
	 * @param player the player
	 * @return the boolean
	 */
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

	/**
	 * Show waiting list to {@link CommandSender}
	 *
	 * @param sender the sender
	 */
	public void showWaiting (CommandSender sender) {
		if (dropIn.size() < 1) {
			Common.tell(sender, pre + Config.noAppt);
			return;
		}
		dropIn.sort(Appointment::compareTo);
		String pattern = "E, HH:mm";
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
		for (Appointment a : dropIn) {
			Common.tell(sender, pre + CFG.getString("list-format")
					.replace("\\n", System.getProperty("line.separator") + pre)
					.replaceAll("#", String.valueOf(a.getNumber()))
					.replaceAll("%player%", getOfflinePlayer(UUID.fromString(a.getPlayerId())).getName())
					.replaceAll("%time%", dtf.format(a.getTime()))
					.replaceAll("%reason%", a.getReason()));
		}
	}

	private Optional <Appointment> getByNumber (int key) {
		for (Appointment a : dropIn) {
			if (a.getNumber() == key) {
				return Optional.of(a);
			}
		}
		return Optional.empty();
	}

	private Optional <Appointment> getByUUID (String playerId) {
		for (Appointment a : dropIn) {
			if (a.getPlayerId().equalsIgnoreCase(playerId)) {
				return Optional.of(a);
			}
		}
		return Optional.empty();
	}

	public String clear (int key) {
		String result = null;
		Optional <Appointment> test;
		test = getByNumber(key);
		if (test.isPresent()) {
			result = getOfflinePlayer(UUID.fromString(test.get().getPlayerId())).getName() +
					" had been removed from the queue."; // todo move to messages config
			dropIn.remove(test.get());
		}
		else {
			result = "A ticket with that number was not found";
		}
		return result;
	}

	public void wipe (CommandSender sender) {
		if (dropIn.size() > 1) {
			dropIn.clear();
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
	void addVet (Player player) {
		onlineVets.add(player);
	}

	/**
	 * Remove a vet from the list to be notified
	 * of waiting appointments.
	 *
	 * @param player Player to remove from the list.
	 */
	void removeVet (Player player) {
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
		if (time.matches(".{4,5}[p]")) { // todo fix detection of 12:00p as midnight
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

	public void closeData () {
		saveAll();
		dropIn.clear();
		appointmentList.clear();
		onlineVets.clear();
	}
}
