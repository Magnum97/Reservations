/*
 *  Base with comment and header features created by Log-out
 *  https://bukkit.org/threads/tut-custom-yaml-configurations-with-comments.142592/
 *  Updated by Magnum to auto update config files from resources
 */

package me.magnum.reservations.util;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class SimpleConfigManager {

	private JavaPlugin plugin;

	/**
	 * Manage custom configurations and files
	 *
	 * @param plugin instance of plugin to manage for
	 */
	public SimpleConfigManager (JavaPlugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Get a new config file with no headers.
	 *
	 * @param filePath    Path to file
	 * @param useDefaults true will use defaults from resource
	 * @return - New SimpleConfig
	 */
	public SimpleConfig getNewConfig (String filePath, boolean useDefaults) {
		return this.getNewConfig(filePath, useDefaults, null);
	}

	/**
	 * Get new configuration with header.
	 * <p>It will be blank unless <b>useDefaults</b> is true
	 * <i>and</i> include it in resources.</p>
	 *
	 * @param filePath    Path and filename
	 * @param useDefaults True will check for default resource.
	 *                    False for blank/custom a config file.
	 * @param header      String array to use as a header. Can be null
	 * @return SimpleConfig
	 */
	public SimpleConfig getNewConfig (String filePath, boolean useDefaults, String[] header) {

		File file = this.getConfigFile(filePath);

		if (! file.exists()) {
			this.prepareFile(filePath);

			if (header != null && header.length != 0) {
				this.setHeader(file, header);
			}

		}

		return new SimpleConfig(this.getConfigContent(filePath), file, this.getCommentsNum(file), useDefaults, plugin);

	}

	/**
	 * Get configuration file from string
	 *
	 * @param file - File path
	 * @return - New file object
	 */
	 File getConfigFile (String file) {

		if (file == null || file.isEmpty()) {
			return null;
		}

		File configFile;

		if (file.contains("/")) {

			if (file.startsWith("/")) {
				configFile = new File(plugin.getDataFolder() + file.replace("/", File.separator));
			}
			else {
				configFile = new File(plugin.getDataFolder() + File.separator + file.replace("/", File.separator));
			}

		}
		else {
			configFile = new File(plugin.getDataFolder(), file);
		}

		return configFile;

	}

	/**
	 * Create new file for config and copy resource into it
	 *
	 * @param filePath - Path to file
	 * @param resource - Resource to copy
	 */
	 void prepareFile (String filePath, String resource) {

		File file = this.getConfigFile(filePath);

		if (file.exists()) {
			return;
		}

		try {
			file.getParentFile().mkdirs();
			file.createNewFile();

			if (resource != null && ! resource.isEmpty()) {
				this.copyResource(plugin.getResource(resource), file);
			}

		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Create new file for config without resource
	 *
	 * @param filePath - File to create
	 */
	 void prepareFile (String filePath) {
		this.prepareFile(filePath, null);
	}

	/**
	 * Adds nice formatted header block to config
	 *
	 * @param file   - Config file
	 * @param header - Header lines
	 */
	void setHeader (File file, String[] header) {

		if (! file.exists()) {
			return;
		}

		try {
			String currentLine;
			StringBuilder config = new StringBuilder();
			BufferedReader reader = new BufferedReader(new FileReader(file));

			while ((currentLine = reader.readLine()) != null) {
				config.append(currentLine + "\n");
			}

			reader.close();
			config.append("# +----------------------------------------------------+ #\n");

			for (String line : header) {

				if (line.length() > 50) {
					continue;
				}

				int lenght = (50 - line.length()) / 2;
				StringBuilder finalLine = new StringBuilder(line);

				for (int i = 0; i < lenght; i++) {
					finalLine.append(" ");
					finalLine.reverse();
					finalLine.append(" ");
					finalLine.reverse();
				}

				if (line.length() % 2 != 0) {
					finalLine.append(" ");
				}

				config.append("# < " + finalLine.toString() + " > #\n");

			}

			config.append("# +----------------------------------------------------+ #");

			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(this.prepareConfigString(config.toString()));
			writer.flush();
			writer.close();

		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Read file and make comments SnakeYAML friendly
	 *
	 * @param file - Path to file
	 * @return - File as Input Stream
	 */
	 InputStreamReader getConfigContent (File file) {

		if (! file.exists()) {
			return null;
		}

		try {
			int commentNum = 0;

			String addLine;
			String currentLine;
			String pluginName = this.getPluginName();

			StringBuilder whole = new StringBuilder();
			BufferedReader reader = new BufferedReader(new FileReader(file));

			while ((currentLine = reader.readLine()) != null) {

				if (currentLine.startsWith("#")) {
					addLine = currentLine.replaceFirst("#", pluginName + "_COMMENT_" + commentNum + ":");
					whole.append(addLine + "\n");
					commentNum++;

				}
				else {
					whole.append(currentLine + "\n");
				}

			}

			String config = whole.toString();
			InputStream configStream = new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8));
			InputStreamReader configStreamReader = new InputStreamReader(configStream);
			reader.close();
			return configStreamReader;

		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}


	/**
	 * Get comments from file
	 *
	 * @param file - File
	 * @return - Comments number
	 */
	 int getCommentsNum (File file) {

		if (! file.exists()) {
			return 0;
		}

		try {
			int comments = 0;
			String currentLine;

			BufferedReader reader = new BufferedReader(new FileReader(file));

			while ((currentLine = reader.readLine()) != null) {

				if (currentLine.startsWith("#")) {
					comments++;
				}

			}

			reader.close();
			return comments;

		}
		catch (IOException e) {
			e.printStackTrace();
			return 0;
		}

	}

	/**
	 * Get config content from file
	 *
	 * @param filePath - Path to file
	 * @return - readied file
	 */
	 InputStreamReader getConfigContent (String filePath) {
		return this.getConfigContent(this.getConfigFile(filePath));
	}


	 String prepareConfigString (String configString) {

		int lastLine = 0;
		int headerLine = 0;

		String[] lines = configString.split("\n");
		StringBuilder config = new StringBuilder();

		for (String line : lines) {

			if (line.startsWith(this.getPluginName() + "_COMMENT")) {
				String comment = "#" + line.trim().substring(line.indexOf(":") + 1);

				if (comment.startsWith("# +-")) {

					/*
					 * If header line = 0 then it is
					 * header start, if it's equal
					 * to 1 it's the end of header
					 */

					if (headerLine == 0) {
						config.append(comment + "\n");

						lastLine = 0;
						headerLine = 1;

					}
					else if (headerLine == 1) {
						config.append(comment + "\n\n");

						lastLine = 0;
						headerLine = 0;

					}

				}
				else {

					/*
					 * Last line = 0 - Comment
					 * Last line = 1 - Normal path
					 */

					String normalComment;

					if (comment.startsWith("# ' ")) {
						normalComment = comment.substring(0, comment.length() - 1).replaceFirst("# ' ", "# ");
					}
					else {
						normalComment = comment;
					}

					if (lastLine == 0) {
						config.append(normalComment + "\n");
					}
					else if (lastLine == 1) {
						config.append("\n" + normalComment + "\n");
					}

					lastLine = 0;

				}

			}
			else {
				config.append(line + "\n");
				lastLine = 1;
			}

		}

		return config.toString();

	}


	/**
	 * Saves configuration to file
	 *
	 * @param configString - Config string
	 * @param file         - Config file
	 */
	 void saveConfig (String configString, File file) {
		String configuration = this.prepareConfigString(configString);

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(configuration);
			writer.flush();
			writer.close();

		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Get the name of the plugin this instance of {@link SimpleConfigManager} is
	 * registered to.
	 *
	 * @return the plugin name
	 */
	public String getPluginName () {
		return plugin.getDescription().getName();
	}

	/**
	 * Copy resource from Input Stream to file
	 *
	 * @param resource - Resource from .jar
	 * @param file     - File to write
	 */
	private void copyResource (InputStream resource, File file) {

		try {
			OutputStream out = new FileOutputStream(file);

			int lenght;
			byte[] buf = new byte[1024];

			while ((lenght = resource.read(buf)) > 0) {
				out.write(buf, 0, lenght);
			}

			out.close();
			resource.close();

		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

}
