package com.deadsimple.majoritysleeps;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class MainConfig {

	private FileConfiguration config;
	private final JavaPlugin plugin;
	private final Logger logger;

	private double threshold;
	private boolean showMessage;
	private boolean broadcast;
	private String message;

	MainConfig(final JavaPlugin plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfig();
		this.logger = plugin.getLogger();

		config.addDefault("threshold", 0.5);
		config.addDefault("wake-up-call.message", "Good morning!");
		config.addDefault("wake-up-call.display", true);
		config.addDefault("wake-up-call.broadcast", false);
		plugin.saveDefaultConfig();

		reloadConfig();
	}

	public Logger getLogger() {
		return logger;
	}

	private void reloadConfig() {
		plugin.reloadConfig();
		this.config = plugin.getConfig();

		threshold = config.getRoot().getDouble("threshold");
		showMessage = config.getRoot().getBoolean("wake-up-call.display");
		message = config.getRoot().getString("wake-up-call.message");
		broadcast = config.getRoot().getBoolean("wake-up-call.broadcast");
	}

	public double getThreshold() {
		return threshold;
	}

	public String getMessage() {
		return message;
	}

	public boolean getShowMessage() {
		return showMessage;
	}

	public boolean getShouldBroadcast() {
		return broadcast;
	}
}
