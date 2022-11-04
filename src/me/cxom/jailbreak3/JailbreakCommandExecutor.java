package me.cxom.jailbreak3;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.cxom.jailbreak3.arena.JailbreakArena;
import me.cxom.jailbreak3.game.JailbreakGame;
import net.punchtree.minigames.arena.creation.ArenaManager;
import net.punchtree.minigames.game.GameManager;
import net.punchtree.minigames.utility.player.PlayerProfile;

public class JailbreakCommandExecutor implements CommandExecutor {

	private static final String SHOWJAILS_PERMISSION = "jailbreak.showjails";
	private final GameManager<JailbreakGame> jailbreakGameManager;
	private final Logger logger;
	private final JailbreakRegionIllustrator regionIllustrator;
	private final ArenaManager<JailbreakArena> arenaManager;

	public JailbreakCommandExecutor(GameManager<JailbreakGame> jailbreakGameManager, Logger logger, JailbreakRegionIllustrator regionIllustrator, ArenaManager<JailbreakArena> arenaManager) {
		this.jailbreakGameManager = jailbreakGameManager;
		this.logger = logger;
		this.regionIllustrator = regionIllustrator;
		this.arenaManager = arenaManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		if (! (sender instanceof Player)) return true;
		Player player = (Player) sender;
		
		if (args.length == 0){
			if (PlayerProfile.isSaved(player)) {
				logger.severe(player.getName() + " tried to join a game but has a saved inventory!");
				return true;
			}
			
			jailbreakGameManager.showMenuTo(player);
			
			return true;
		} else if (args.length >= 2 && args[0].equalsIgnoreCase("showjails") && player.hasPermission(SHOWJAILS_PERMISSION)) {
			if (! arenaManager.isArena(args[1])){
				player.sendMessage(Jailbreak.CHAT_PREFIX + ChatColor.RED + " There is no game/arena named " + args[1] + "!");
			} else {
				regionIllustrator.illustrateRegion(player, arenaManager.getArena(args[1]));
			}
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
	
}
