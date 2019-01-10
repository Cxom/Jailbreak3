package me.cxom.jailbreak3.player;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.cxom.jailbreak3.arena.JailbreakTeam;

public class JailbreakPlayer {

	private final UUID uuid;
	
	private boolean free = true;
	private JailbreakTeam team;
	
	public JailbreakPlayer(Player player, JailbreakTeam team){
		this.uuid = player.getUniqueId();
		this.team = team;
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
		return free;
	}
	
	public void setFree(boolean free){
		this.free = free;
	}
	
}
