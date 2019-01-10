package me.cxom.jailbreak3.gui;

import org.bukkit.ChatColor;

import me.cxom.jailbreak3.game.AttackMethod;
import me.cxom.jailbreak3.player.JailbreakPlayer;

public class Killfeed extends ScrollingScoreboard {

	public Killfeed(String title) {
		super(title);
	}
	
	public void sendKill(JailbreakPlayer killer, JailbreakPlayer killed, AttackMethod attackMethod) {
		String killerName = killer.getPlayer().getName();
		String killedName = killed.getPlayer().getName();
		
		String killfeedMessage = String.format("%s%s %s %s%s",
				killer.getTeam().getChatColor(), killerName,
				ChatColor.WHITE + attackMethod.getIcon(),
				killed.getTeam().getChatColor(), killedName);
		killfeedMessage = killfeedMessage.length() > MAX_SCOREBOARD_LENGTH ? killfeedMessage.substring(0, MAX_SCOREBOARD_LENGTH) : killfeedMessage;
		sendMessage(killfeedMessage);
	}

}
