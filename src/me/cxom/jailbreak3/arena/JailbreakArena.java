package me.cxom.jailbreak3.arena;

import java.util.List;

import org.bukkit.Location;

public class JailbreakArena {
	
	private final String name;
	private final Location pregameLobby;
	private final int playersToStart;
	private final List<JailbreakTeam> teams;
	
	public JailbreakArena(String name, Location pregameLobby, int playersToStart, List<JailbreakTeam> teams){
		this.name = name;
		this.pregameLobby = pregameLobby;
		this.playersToStart = playersToStart;
		this.teams = teams;
	}
	
	public String getName(){
		return name;
	}
	
	public Location getPregameLobby(){
		return pregameLobby;
	}
	
	public int getPlayersToStart(){
		return playersToStart;
	}
	
	public List<JailbreakTeam> getTeams(){
		return teams;
	}
	
}
