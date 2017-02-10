package me.cxom.jailbreak3.arena;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.cxom.jailbreak3.Jailbreak;
import me.cxom.jailbreak3.arena.Goal.PlayerOffGoalEvent;
import me.cxom.jailbreak3.arena.Goal.PlayerOnGoalEvent;
import me.cxom.jailbreak3.arena.region.Area;
import me.cxom.jailbreak3.player.JailbreakPlayer;
import me.cxom.jailbreak3.utils.JailbreakColor;

public class JailbreakTeam implements Listener {

	private final String name;
	private final JailbreakColor color;
	
	private final List<Location> spawns;
	private final Goal goal;
	private final List<Location> jailspawns;
	private final Area jails;
	private final Door door;
	
	public JailbreakTeam(String name, JailbreakColor color, 
							List<Location> spawns, Goal goal, List<Location> jailspawns, Area jails, Door door){
		this.name = name;
		this.color = color;
		
		this.spawns = spawns;
		this.goal = goal;
		this.jailspawns = jailspawns;
		this.jails = jails;
		this.door = door;
		
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
	
	private Supplier<Map<JailbreakPlayer, JailbreakTeam>> players;
	
	public void setPlayerSupplier(Supplier<Map<JailbreakPlayer, JailbreakTeam>> players){
		this.players = players;
	}
	
	@EventHandler
	public void onWalkOnGoal(PlayerOnGoalEvent e){
		if (players == null) return;
		if (players.get().containsKey(e.getJailbreakPlayer())){
			JailbreakTeam team = players.get().get(e.getJailbreakPlayer());
			Goal goal = e.getGoal();
			if (goal.equals(team.getGoal())){
				goal.addActive();
				if (!goal.isDefended()){
					team.getDoor().open();
				}
			} else {
				goal.addDefended();
				this.door.close();
			}
		}
	}
	
	@EventHandler
	public void onWalkOffGoal(PlayerOffGoalEvent e){
		if (players == null) return;
		if (players.get().containsKey(e.getJailbreakPlayer())){
			JailbreakTeam team = players.get().get(e.getJailbreakPlayer());
			Goal goal = e.getGoal();
			if (e.getGoal().equals(team.getGoal())){
				goal.removeActive();
				if (!goal.isActive()){
					team.getDoor().close();
				}
			} else {
				goal.removeDefended();
				if (goal.isActive()){
					this.door.open();
				}
			}
		}
	}
	
}
