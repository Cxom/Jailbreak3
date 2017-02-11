package me.cxom.jailbreak3.arena;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.Listener;

import me.cxom.jailbreak3.Jailbreak;
import me.cxom.jailbreak3.arena.region.Area;
import me.cxom.jailbreak3.utils.JailbreakColor;

public class JailbreakTeam implements Listener {

	private final String name;
	private final JailbreakColor color;
	
	private final List<Location> spawns;
	private final Goal goal;
	private final List<Location> jailspawns;
	private final Area jails;
	
	private int members = 0;
	private int alive = 0;
	
	public JailbreakTeam(String name, JailbreakColor color, 
							List<Location> spawns, Goal goal, List<Location> jailspawns, Area jails){
		this.name = name;
		this.color = color;
		
		this.spawns = spawns;
		this.goal = goal;
		this.jailspawns = jailspawns;
		this.jails = jails;
		
		Bukkit.getServer().getPluginManager().registerEvents(this, Jailbreak.getPlugin());
	}
	
	public String getName(){
		return name;
	}
	
	public JailbreakColor getColor(){
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
		return members;
	}
	
	public void setSize(int members){
		this.members = members;
	}
	
	public void incrementSize(){
		members++;
	}
	
	public void decrementSize(){
		members--;
	}
	
	public int getAlive(){
		return alive;
	}
	
	public void setAlive(int alive){
		this.alive = alive;
	}
	
	public void incrementAlive(){
		alive++;
	}
	
	public void decrementAlive(){
		alive--;
	}
	
	@Override
	public boolean equals(Object o){
		if (! (o instanceof JailbreakTeam)) return false;
		JailbreakTeam team = (JailbreakTeam) o;
		return team.getName().equals(name) && team.getGoal().equals(goal);
		// Comparing goals is arbitrary, it just prevents errors from comparing teams from different maps
	}
	
}
