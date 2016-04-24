package com.deadsimple.majoritysleeps;

import com.deadsimple.majoritysleeps.listener.PlayerSleepsListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	@Override
	public void onDisable() {
		super.onDisable();
	}

	@Override
	public void onEnable() {
		super.onEnable();

		Bukkit.getServer().getPluginManager().registerEvents
				(new PlayerSleepsListener(new MainConfig(this)), this);
	}
}
