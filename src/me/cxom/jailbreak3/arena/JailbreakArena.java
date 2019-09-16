package me.cxom.jailbreak3.arena;

import java.util.List;

import org.bukkit.Location;

import net.punchtree.minigames.arena.Arena;

public class JailbreakArena extends Arena {
	
	private final List<JailbreakTeam> teams;
	
	public JailbreakArena(String name, Location pregameLobby, int playersNeededToStart, List<JailbreakTeam> teams){
		super(name, pregameLobby, playersNeededToStart);
		this.teams = teams;
	}
	
	public List<JailbreakTeam> getTeams(){
		return teams;
	}
	
}
