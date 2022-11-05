package me.cxom.jailbreak3.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.cxom.jailbreak3.Jailbreak;
import net.punchtree.minigames.utility.player.PlayerProfile;

public class CommandEvents implements Listener{

	List<String> cmds = new ArrayList<String>(Arrays.asList(new String[] {
			"/m", "/msg", "/message", "/t", "/tell", "/w", "/whisper", "/r",
			"/reply", "/ac", "/helpop", "/leave"}));

	@EventHandler
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {
		Player player = e.getPlayer();
		String command = e.getMessage().toLowerCase() + " ";
		if (PlayerProfile.isSaved(player) && !player.isOp()){
			if (cmds.contains(command.split(" ")[0])) {
				return;
			}else{
				e.setCancelled(true);
				player.sendMessage(Jailbreak.CHAT_PREFIX + ChatColor.RED + "You do not have permission to use non-messaging commands in Jailbreak. If you wish to leave the match, do /leave");
			}
		}
		
	}
	
}
