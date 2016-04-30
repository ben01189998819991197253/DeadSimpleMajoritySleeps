package com.deadsimple.majoritysleeps.listener;

import com.deadsimple.majoritysleeps.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class PlayerSleepsListener implements Listener {

	private Logger logger;

	private List<Player> sleepingPlayers;
	private World world = null;

	private String message;
	private double threshold;
	private boolean shouldBroadcast;
	private boolean showMessage;

	public PlayerSleepsListener(final MainConfig config) {
		logger = config.getLogger();

		sleepingPlayers = new ArrayList<>();

		message = config.getMessage();
		threshold = config.getThreshold();
		shouldBroadcast = config.getShouldBroadcast();
		showMessage = config.getShowMessage();

		if(threshold > 1 || threshold < 0) {
			logger.log(new LogRecord(Level.WARNING, "Invalid parameter set " +
					"for \"threshold\" (" + threshold + "), using \"0.50\"."));
			threshold = 0.5;
		}
	}

	@EventHandler
	public void onPlayerEntersBed(PlayerBedEnterEvent e) {
		Player p = e.getPlayer();

		sleepingPlayers.add(p);
		if(world == null) {
			world = p.getWorld();
		}

		double percentage = percentSleeping() * 100;
		logger.log(new LogRecord(Level.INFO, p.getDisplayName() + " is " +
				"now sleeping (" + percentage + "%/" +
				(threshold * 100) + "%)."));

		if(shouldSkipToDawn()) {
			skipToDawn();
		}
		else {
			world.getPlayers().forEach(player -> player
					.sendMessage("§6§o" + p.getDisplayName() + " is now " +
							"sleeping! " + sleepStatus()));
		}
	}

	@EventHandler
	public void onPlayerLeavesBed(PlayerBedLeaveEvent e) {
		Player p = e.getPlayer();
		sleepingPlayers.remove(p);

		if(isNighttime()) {
			double percentage = percentSleeping() * 100;
			logger.log(new LogRecord(Level.INFO, p.getDisplayName() +
					" is no longer sleeping (" + percentage + "%/" +
					(threshold * 100) + "%)."));
		}

		if(sleepingPlayers.size() > 0 && !shouldSkipToDawn()) {
			world.getPlayers().forEach(player -> player
					.sendMessage("§6§o" + p.getDisplayName() + " is no longer" +
							" sleeping! " + sleepStatus()));
		}
	}

	@EventHandler
	public void onPlayerLeavesServer(PlayerQuitEvent e) {
		if(world != null) {
			if(shouldSkipToDawn()) {
				skipToDawn();
			}
			else if(isNighttime() && sleepingPlayers.size() > 0) {
				double percentage = percentSleeping() * 100;
				logger.log(new LogRecord(Level.INFO, e.getPlayer()
						.getDisplayName() + " left during the night (" +
						percentage + "%/" + (threshold * 100) + "%)."));

				world.getPlayers().forEach(p -> p.sendMessage("§6§o" +
						sleepStatus()));
			}
			// Else it's daytime OR nobody was sleeping
		}
	}

	private boolean isNighttime() {
		return world.getFullTime() >= 12541 && world.getFullTime() <= 23458;
	}

	private String sleepStatus() {
		return "(" + (percentSleeping() * 100) + "%). We need at least " +
				(threshold * 100) + "% sleeping to skip to dawn.";
	}

	private boolean shouldSkipToDawn() {
		return percentSleeping() >= threshold;
	}

	private double percentSleeping() {
		if(world.getPlayers().size() == 0) {return 0.0;}
		return (double)sleepingPlayers.size() /
				world.getPlayers().size();
	}

	private void skipToDawn() {
		logger.log(new LogRecord(Level.INFO, "Skipping to dawn..."));

		world.setTime(0);

		if(shouldBroadcast) {
			Bukkit.broadcastMessage("§6§o" + message);
		}
		else if(showMessage) {
			world.getPlayers().forEach(p -> p.sendMessage("§6§o" + message));
		}
		// Else don't display the message
	}

}
