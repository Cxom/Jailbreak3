package me.cxom.jailbreak3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import me.cxom.jailbreak3.events.CancelledEvents;
import me.cxom.jailbreak3.events.CommandEvents;

public class Jailbreak extends JavaPlugin{
	
	public static final String CHAT_PREFIX = ChatColor.BLUE + "[" + ChatColor.WHITE + "Jailbreak" + ChatColor.BLUE + "]" + ChatColor.RESET;
	
	private static Plugin plugin;
	public static Plugin getPlugin(){ return plugin; }
	
	private static Map<UUID, JailbreakPlayer> players = new HashMap<>();
	
	@Override
	public void onEnable(){
		plugin = this;
		Bukkit.getServer().getPluginManager().registerEvents(new CancelledEvents(), getPlugin());
		Bukkit.getServer().getPluginManager().registerEvents(new CommandEvents(), getPlugin());
		//register events
	}
	
	@Override
	public void onDisable(){
		//restore inventories, locations
	} 
	
	public static boolean isPlayer(Player player){ return isPlayer(player.getUniqueId()); }
	public static boolean isPlayer(UUID uuid){
		return players.containsKey(uuid);
	}
	
	public static JailbreakPlayer getPlayer(Player player){ return getPlayer(player.getUniqueId()); }
	public static JailbreakPlayer getPlayer(UUID uuid){
		return players.get(uuid);
	}
	
	
}
