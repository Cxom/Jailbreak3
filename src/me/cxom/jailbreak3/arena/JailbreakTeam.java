package me.cxom.jailbreak3.arena;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import me.cxom.jailbreak3.player.JailbreakPlayer;
import net.punchtree.minigames.region.Area;
import net.punchtree.util.color.PunchTreeColor;

public class JailbreakTeam {

	private final String name;
	private final PunchTreeColor color;
	
	private final List<Location> spawns;
	private final Goal goal;
	private final List<Location> jailspawns;
	private final Area jails;
	
	private Set<JailbreakPlayer> jplayers = new HashSet<>();
	
	public JailbreakTeam(String name, PunchTreeColor color, 
							List<Location> spawns, Goal goal, List<Location> jailspawns, Area jails){
		this.name = name;
		this.color = color;
		
		this.spawns = spawns;
		this.goal = goal;
		this.jailspawns = jailspawns;
		this.jails = jails;	
	}
	
	public void addPlayer(JailbreakPlayer jp) {
		jplayers.add(jp);
	}
	
	public void removePlayer(JailbreakPlayer jp) {
		jplayers.remove(jp);
	}
	
	public String getName(){
		return name;
	}
	
	public PunchTreeColor getColor(){
		return color;
	}
	
	public ChatColor getChatColor(){
		return color.getChatColor();
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
	
	public int getSize(){
		return jplayers.size();
	}
	
	public int getAlive() {
		int alive = 0;
		for (JailbreakPlayer jp : jplayers) {
			if (jp.isFree()) {
				++alive;
			}
		}
		return alive;
	}
	
	@Override
	public boolean equals(Object o){
		if (! (o instanceof JailbreakTeam)) return false;
		JailbreakTeam team = (JailbreakTeam) o;
		return team.getName().equals(name) && team.getGoal().equals(goal);
		// Comparing goals is arbitrary, it just prevents errors from comparing teams from different maps
	}

	public void reset() {
		jplayers.clear();
		getGoal().setActive(0);
		getGoal().setDefended(0);
		getGoal().getDoor().close();
	}
	
}
