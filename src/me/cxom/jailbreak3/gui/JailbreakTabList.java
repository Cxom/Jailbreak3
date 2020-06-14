package me.cxom.jailbreak3.gui;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.cxom.jailbreak3.player.JailbreakPlayer;

public class JailbreakTabList /* implements RabbitGameObserver */ {

	private Set<Player> players = new HashSet<>();
	
	public void addPlayer(JailbreakPlayer jailbreakPlayer) {
		Player player = jailbreakPlayer.getPlayer();
		players.add(player);
		updatePlayer(jailbreakPlayer);
	}
	
	public void updatePlayer(JailbreakPlayer jailbreakPlayer) {
		Player player = jailbreakPlayer.getPlayer();
		player.setPlayerListName(jailbreakPlayer.getColor().getChatColor() + player.getName()); // + " " + ChatColor.GRAY + jailbreakPlayer.getKills());
	}
	
	public void removePlayer(Player player) {
		resetPlayerListName(player);
		players.remove(player);
	}
	
	public void removeAll() {
		players.forEach(this::resetPlayerListName);
		players.clear();
	}
	
	/* We don't save and restore what was in the tablist before this plugin
	 * because it's too hard to predict the way other plugins use the tablist
	 */
	private void resetPlayerListName(Player player) {
		player.setPlayerListName(player.getName());
	}
	
//	@Override
//	public void onGameReset(Collection<RabbitPlayer> rPlayers) {
//		
//	}
	
}
