package me.cxom.jailbreak3.arena;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.cxom.jailbreak3.Jailbreak;
import me.cxom.jailbreak3.JailbreakPlayer;

public class Goal implements Listener{

	private final Location goal;
	private final double radius;
	private final Consumer<JailbreakPlayer> toggleOn;
	private final Consumer<JailbreakPlayer> toggleOff;
	
	public Goal(Location goal, double radius, Consumer<JailbreakPlayer> toggleOn, Consumer<JailbreakPlayer> toggleOff){
		this.goal = goal;
		this.radius = radius;
		this.toggleOn = toggleOn;
		this.toggleOff = toggleOff;
		Bukkit.getServer().getPluginManager().registerEvents(this, Jailbreak.getPlugin());
	}
	
	public Location getLocation(){
		return goal;
	}
	
	public double getRadius(){
		return radius;
	}
	
	public boolean isOnGoal(Location loc){
		if (distance(goal, loc) <= radius){
			return true;
		} else {
			return false;
		}
	}
	
	private static double distance(Location a, Location b){
		return Math.sqrt(
			   Math.pow(b.getX() - a.getX(), 2) +
			   Math.pow(b.getY() - a.getY(), 2) +
			   Math.pow(b.getZ() - a.getZ(), 2) );
	}
	
	@EventHandler
	public void onGoalTrigger(PlayerMoveEvent e){
		if(Jailbreak.isPlayer(e.getPlayer())){
			if(toggleOn != null && isOnGoal(e.getTo()) && !isOnGoal(e.getFrom())){
				toggleOn.accept(Jailbreak.getPlayer(e.getPlayer()));
			} else if (toggleOff != null && isOnGoal(e.getFrom()) && !isOnGoal(e.getTo())){
				toggleOff.accept(Jailbreak.getPlayer(e.getPlayer()));
			}
		}
	}
	
}
