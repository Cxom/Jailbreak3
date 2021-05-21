package me.cxom.jailbreak3.player;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.cxom.jailbreak3.arena.JailbreakTeam;
import me.cxom.jailbreak3.game.JailbreakGame;
import net.punchtree.minigames.utility.color.ColoredPlayer;
import net.punchtree.util.color.PunchTreeColor;

public class JailbreakPlayer implements ColoredPlayer {

	private final UUID uuid;
	
//	private boolean free = true;
	private JailbreakTeam team;
	private JailbreakGame game;
	
	public JailbreakPlayer(Player player, JailbreakTeam team, JailbreakGame game){
		this.uuid = player.getUniqueId();
		this.team = team;
		this.game = game;
	}
	
	public UUID getUniqueId(){
		return uuid;
	}
	
	public Player getPlayer(){
		return Bukkit.getPlayer(uuid);
	}
	
	public JailbreakTeam getTeam() {
		return team;
	}
	
	public boolean isFree(){
		return !game.isInJail(getPlayer().getLocation());
	}

	@Override
	public PunchTreeColor getColor() {
		return team.getColor();
	}
	
}
