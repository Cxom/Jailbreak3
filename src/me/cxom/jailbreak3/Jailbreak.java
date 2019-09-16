package me.cxom.jailbreak3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import me.cxom.jailbreak3.arena.JailbreakArena;
import me.cxom.jailbreak3.arena.config.ArenaManager;
import me.cxom.jailbreak3.events.CancelledEvents;
import me.cxom.jailbreak3.events.CommandEvents;
import me.cxom.jailbreak3.events.custom.JailbreakDeathEventCaller;
import me.cxom.jailbreak3.game.JailbreakGame;
import me.cxom.jailbreak3.player.JailbreakMenu;
import me.cxom.jailbreak3.player.JailbreakPlayer;
import net.punchtree.minigames.utility.player.PlayerProfile;

public class Jailbreak extends JavaPlugin{
	
	public static final String CHAT_PREFIX = ChatColor.BLUE + "[" + ChatColor.WHITE + "Jailbreak" + ChatColor.BLUE + "]" + ChatColor.RESET + " ";
	
	private static Plugin plugin;
	public static Plugin getPlugin(){ return plugin; }
	
	private static Map<UUID, JailbreakPlayer> players = new HashMap<>();
	
	private static Map<String, JailbreakGame> games = new HashMap<>();
	
	@Override
	public void onEnable(){
		plugin = this;
		Bukkit.getServer().getPluginManager().registerEvents(new CancelledEvents(), getPlugin());
		Bukkit.getServer().getPluginManager().registerEvents(new CommandEvents(), getPlugin());
		Bukkit.getServer().getPluginManager().registerEvents(new JailbreakDeathEventCaller(), getPlugin());
		Bukkit.getServer().getPluginManager().registerEvents(new JailbreakMenu(), getPlugin());
		//register events
		ArenaManager.loadArenas();
		for(JailbreakArena arena : ArenaManager.getArenas()){
			games.put(arena.getName(), new JailbreakGame(arena));
		}
	}
	
	@Override
	public void onDisable(){
		for (JailbreakGame game : games.values()){
			game.forceStop();
		}
		PlayerProfile.restoreAll(); // this should be redundant (forcestop should restore all inventories)
		//deal with arena files (creation/modification saving)
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		if (args.length == 0){
			if (! (sender instanceof Player)) return true;
			if (PlayerProfile.isSaved((Player) sender)) return true;
			((Player) sender).openInventory(JailbreakMenu.getMenu());
			return true;
		} else if (args.length >= 2 && args[0].equalsIgnoreCase("join")){
			if (! (sender instanceof Player)) return true;
			Player player = (Player) sender;
			if (! games.containsKey(args[1])){
				player.sendMessage(Jailbreak.CHAT_PREFIX + ChatColor.RED + " There is no game/arena named " + args[1] + "!");
				return true;
			} else {
				games.get(args[1]).getLobby().addPlayerIfPossible(player);
				return true;
			}
		}
		
		return true;
	}
	
	public static boolean isPlayer(Player player){ return isPlayer(player.getUniqueId()); }
	public static boolean isPlayer(UUID uuid){
		return players.containsKey(uuid);
	}
	
	public static JailbreakPlayer getPlayer(Player player){ return getPlayer(player.getUniqueId()); }
	public static JailbreakPlayer getPlayer(UUID uuid){
		return players.get(uuid);
	}
	
	public static void addPlayer(JailbreakPlayer jp){
		players.put(jp.getUniqueId(), jp);
	}
	
	public static void removePlayer(JailbreakPlayer jp){ removePlayer(jp.getUniqueId()); }
	public static void removePlayer(Player player){ removePlayer(player.getUniqueId()); }
	public static void removePlayer(UUID uuid){
		players.remove(uuid);
	}
	
	public static JailbreakGame getGame(String name){
		return games.get(name);
	}

	public static Map<String, JailbreakGame> getGameMap(){
		return games;
	}
	
}
