package me.cxom.jailbreak3.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerUtils {

	public static void perfectStats(Player player){
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setSaturation(20);
		player.setExhaustion(0);
		player.setFireTicks(0);
	}
	
	public static void broadcast(Iterable<? extends CommandSender> audience, String msg) {
		for(CommandSender cm : audience) {
			cm.sendMessage(msg);
		}
	}
	
}
