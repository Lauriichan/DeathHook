package org.playuniverse.minecraft.deathhook;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathHook extends JavaPlugin {
	
	private ConfigManager configManager;
	
	@Override
	public void onEnable() {
		this.configManager = new ConfigManager(getDataFolder());
		Bukkit.getPluginManager().registerEvents(new DeathListener(configManager), this);
	}
	
	@Override
	public void onDisable() {
		configManager.exit();
	}

}
