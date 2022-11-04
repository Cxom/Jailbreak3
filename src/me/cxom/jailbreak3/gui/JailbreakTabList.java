package me.cxom.jailbreak3.gui;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import me.cxom.jailbreak3.game.JailbreakGame;
import me.cxom.jailbreak3.player.JailbreakPlayer;
import net.kyori.adventure.text.Component;

public class JailbreakTabList /* implements RabbitGameObserver */ {
	
	private Set<Player> players = new HashSet<>();
	
	private final Scoreboard scoreboard;
	private Objective playerlist;
	
	public JailbreakTabList(Scoreboard scoreboard, String title) {
		this.scoreboard = scoreboard;
		playerlist = scoreboard.registerNewObjective("playerlist", "dummy", title);
		playerlist.setDisplaySlot(DisplaySlot.PLAYER_LIST);
	}
	
	public void addPlayer(JailbreakPlayer jailbreakPlayer) {
		Player player = jailbreakPlayer.getPlayer();
		player.setScoreboard(scoreboard);
		players.add(player);
		updatePlayer(jailbreakPlayer);
	}
	
	public void updatePlayer(JailbreakPlayer jailbreakPlayer) {
		Player player = jailbreakPlayer.getPlayer();
		player.setPlayerListName(jailbreakPlayer.getColor().getChatColor() + player.getName()); // + " " + ChatColor.GRAY + jailbreakPlayer.getKills());
		String teamString = jailbreakPlayer.getColor().getChatColor() + jailbreakPlayer.getTeam().getName();
		player.sendPlayerListHeaderAndFooter(Component.text(teamString), Component.text(teamString));
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
		player.setPlayerListHeaderFooter("", "");
	}
	
//	@Override
//	public void onGameReset(Collection<RabbitPlayer> rPlayers) {
//		
//	}
	
}
