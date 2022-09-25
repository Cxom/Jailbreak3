package me.cxom.jailbreak3;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.punchtree.minigames.lobby.PerMapLegacyLobby;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import me.cxom.jailbreak3.arena.JailbreakArena;
import me.cxom.jailbreak3.arena.config.JailbreakArenaLoader;
import me.cxom.jailbreak3.events.CancelledEvents;
import me.cxom.jailbreak3.events.CommandEvents;
import me.cxom.jailbreak3.events.custom.JailbreakDeathEventCaller;
import me.cxom.jailbreak3.game.JailbreakGame;
import me.cxom.jailbreak3.player.JailbreakPlayer;
import net.punchtree.minigames.arena.creation.ArenaManager;
import net.punchtree.minigames.game.GameManager;
import net.punchtree.minigames.utility.player.PlayerProfile;

public class Jailbreak extends JavaPlugin{
	
	public static final String CHAT_PREFIX = ChatColor.BLUE + "[" + ChatColor.WHITE + "Jailbreak" + ChatColor.BLUE + "]" + ChatColor.RESET + " ";
	
	private static Plugin plugin;
	public static Plugin getPlugin(){ return plugin; }
	
	private File jailbreakArenaFolder;
	
	private ArenaManager<JailbreakArena> jailbreakArenaManager;
	
	private GameManager<JailbreakGame> jailbreakGameManager;
	
	private static Map<UUID, JailbreakPlayer> players = new HashMap<>();
	
	private JailbreakCommandExecutor commandExecutor;
	
	@Override
	public void onEnable(){
		plugin = this;
		
		jailbreakArenaFolder = new File(getDataFolder().getAbsolutePath() + File.separator + "Arenas");
		jailbreakArenaManager = new ArenaManager<>(jailbreakArenaFolder, JailbreakArenaLoader::load);
		jailbreakGameManager = new GameManager<>(CHAT_PREFIX + "Games");
		
		registerEvents();
		
		createAllGames();
		
		commandExecutor = new JailbreakCommandExecutor(jailbreakGameManager, getLogger());
		getCommand("jailbreak").setExecutor(commandExecutor);
	}
	
	private void registerEvents() {
		Bukkit.getServer().getPluginManager().registerEvents(new CancelledEvents(), getPlugin());
		Bukkit.getServer().getPluginManager().registerEvents(new CommandEvents(), getPlugin());
		Bukkit.getServer().getPluginManager().registerEvents(new JailbreakDeathEventCaller(), getPlugin());
	}
	
	private void createAllGames() {
		jailbreakArenaManager.loadArenas();
		jailbreakArenaManager.getArenas().forEach(jailbreakArena -> {
			JailbreakGame game = new JailbreakGame(jailbreakArena);
			jailbreakGameManager.addGame(jailbreakArena.getName(), game, new PerMapLegacyLobby(game, PlayerProfile::restore, Jailbreak.CHAT_PREFIX));
		});
	}
	
	@Override
	public void onDisable(){
		jailbreakGameManager.stopAllGames();
		// TODO figure out why we put this here
		PlayerProfile.restoreAll(); // this should be redundant (forcestop should restore all inventories)
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
	
}
