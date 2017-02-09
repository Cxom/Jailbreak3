package me.cxom.jailbreak3.player;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class JailbreakPlayer {

	private final UUID uuid;
	
	private boolean free = true;
	//(AroundTheWorldPlayer)-Some sort of LanguageProfile composition?
	
	public JailbreakPlayer(Player player){
		this.uuid = player.getUniqueId();
	}
	
	public UUID getUniqueId(){
		return uuid;
	}
	
	public Player getPlayer(){
		return Bukkit.getPlayer(uuid);
	}
	
	public boolean isFree(){
		return free;
	}
	
	public void setFree(boolean free){
		this.free = free;
	}
	
}
