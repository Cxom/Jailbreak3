package me.cxom.jailbreak3.arena;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import me.cxom.jailbreak3.arena.region.Area;

public class JailbreakTeam {

	private final String name;
	private final ChatColor chatColor;
	
	private final List<Location> spawns;
	private final Goal goal;
	private final List<Location> jailspawns;
	private final Area jails;
	private final Door door;
	
	public JailbreakTeam(String name, ChatColor cc, 
							List<Location> spawns, Goal goal, List<Location> jailspawns, Area jails, Door door){
		this.name = name;
		this.chatColor = cc;
		
		this.spawns = spawns;
		this.goal = goal;
		this.jailspawns = jailspawns;
		this.jails = jails;
		this.door = door;
	}
	
	public String getName(){
		return name;
	}
	
	public ChatColor getChatColor(){
		return chatColor;
	}
	
	public List<Location> getSpawns(){
		return spawns;
	}

	public Goal getGoal() {
		return goal;
	}

	public List<Location> getJailspawns() {
		return jailspawns;
	}

	public Area getJails() {
		return jails;
	}

	public Door getDoor() {
		return door;
	}
	
	@Override
	public boolean equals(Object o){
		if (! (o instanceof JailbreakTeam)) return false;
		JailbreakTeam team = (JailbreakTeam) o;
		return team.getName().equals(name) && team.getGoal().equals(goal);
		// Comparing goals is arbitrary, it just prevents errors from comparing teams from different maps
	}
	
}
