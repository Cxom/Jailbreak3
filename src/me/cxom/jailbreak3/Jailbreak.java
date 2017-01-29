package me.cxom.jailbreak3;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Jailbreak extends JavaPlugin{
	
	private static Plugin plugin;
	public static Plugin getPlugin(){ return plugin; }
	
	@Override
	public void onEnable(){
		plugin = this;
		//register events
	}
	
	@Override
	public void onDisable(){
		//restore inventories, locations
	}
	
	
}
