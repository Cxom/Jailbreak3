package me.cxom.jailbreak3;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.cxom.jailbreak3.arena.JailbreakArena;
import me.cxom.jailbreak3.arena.JailbreakTeam;
import net.md_5.bungee.api.ChatColor;
import net.punchtree.minigames.region.RegionVisualizer;

public class JailbreakRegionIllustrator implements Listener {

	private Player player;
	private JailbreakArena arena;
	
	public void illustrateRegion(Player player, JailbreakArena arena) {
		this.player = player;
		this.arena = arena;
		for (JailbreakTeam team : arena.getTeams()) {
			RegionVisualizer.drawArea(team.getJails(), team.getColor());
		}
	}
	
	public void reset() {
		this.player = null;
		this.arena = null;
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (player == null || arena == null) return;
		for (JailbreakTeam team : arena.getTeams()) {
			if (team.getJails().contains(event.getTo())) {
				String locationString = ChatColor.GRAY + String.format("(%.3f, %.3f, %.3f)", event.getTo().getX(), event.getTo().getY(), event.getTo().getZ()) + ChatColor.RESET;
				player.sendMessage("In " + team.getChatColor() + team.getName() + ChatColor.RESET + "'s jail " + locationString + "!");
			}
		}
	}
	
}
