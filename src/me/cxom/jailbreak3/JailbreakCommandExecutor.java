package me.cxom.jailbreak3;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.cxom.jailbreak3.game.JailbreakGame;
import net.punchtree.minigames.game.GameManager;
import net.punchtree.minigames.utility.player.PlayerProfile;

public class JailbreakCommandExecutor implements CommandExecutor {

	private final GameManager<JailbreakGame> jailbreakGameManager;
	private final Logger logger;
	
	public JailbreakCommandExecutor(GameManager<JailbreakGame> jailbreakGameManager, Logger logger) {
		this.jailbreakGameManager = jailbreakGameManager;
		this.logger = logger;
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
