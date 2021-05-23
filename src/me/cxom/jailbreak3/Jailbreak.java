package me.cxom.jailbreak3;

import java.io.File;
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
	
	@Override
	public void onEnable(){
		plugin = this;
		
		jailbreakArenaFolder = new File(getDataFolder().getAbsolutePath() + File.separator + "Arenas");
		jailbreakArenaManager = new ArenaManager<>(jailbreakArenaFolder, JailbreakArenaLoader::load);
		jailbreakGameManager = new GameManager<>(CHAT_PREFIX + "Games");
		
		registerEvents();
		
		createAllGames();
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
			jailbreakGameManager.addGame(jailbreakArena.getName(), game, game.getLobby());
		});
	}
	
	@Override
	public void onDisable(){
		jailbreakGameManager.stopAllGames();
		// TODO figure out why we put this here
		PlayerProfile.restoreAll(); // this should be redundant (forcestop should restore all inventories)
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		if (! (sender instanceof Player)) return true;
		Player player = (Player) sender;
		
		if (args.length == 0){
			if (PlayerProfile.isSaved(player)) {
				getLogger().severe(player.getName() + " tried to join a game but has a saved inventory!");
				return true;
			}
			
			jailbreakGameManager.showMenuTo(player);
			
			return true;
		} else if (args.length >= 2 && args[0].equalsIgnoreCase("join")){
			if (! jailbreakGameManager.hasGame(args[1])){
				player.sendMessage(Jailbreak.CHAT_PREFIX + ChatColor.RED + " There is no game/arena named " + args[1] + "!");
				return true;
			} else {
				jailbreakGameManager.addPlayerToGameLobby(args[1], player);
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
	
}
