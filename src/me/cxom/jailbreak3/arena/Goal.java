package me.cxom.jailbreak3.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.cxom.jailbreak3.Jailbreak;
import me.cxom.jailbreak3.events.custom.JailbreakDeathEvent;
import me.cxom.jailbreak3.player.JailbreakPlayer;
import net.punchtree.minigames.arena.Door;

public class Goal implements Listener{

	private final Location location;
	private final double radius;
	private final Door door;
	
	private int active = 0;
	private int defended = 0;
	
	public Goal(Location goal, double radius, Door door){
		this.location = goal;
		this.radius = radius;
		this.door = door;
		Bukkit.getServer().getPluginManager().registerEvents(this, Jailbreak.getPlugin());
	}
	
	public Location getLocation(){
		return location;
	}
	
	public double getRadius(){
		return radius;
	}
	
	public Door getDoor(){
		return door;
	}
	
	public boolean isActive(){
		return active > 0;
	}
	
	public boolean isDefended(){
		return defended > 0;
	}
	
	public void addActive(){
		active++;
	}
	
	public void addDefended(){
		defended++;
	}
	
	public void removeActive(){
		active--;
	}
	
	public void removeDefended(){
		defended--;
	}
	
	public void setActive(int active){
		this.active = active;
	}
	
	public void setDefended(int defended){
		this.defended = defended;
	}
	
	public boolean isOnGoal(Location loc){
		if (distance(location, loc) <= radius){
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean equals(Object o){
		if (! (o instanceof Goal)) return false;
		Goal g = (Goal) o;
		return location == g.getLocation() && radius == g.getRadius();
	}
	
	private static class GoalEvent extends Event {
	    ////
		private static final HandlerList handlers = new HandlerList();
		
		public HandlerList getHandlers() {
			return handlers;
		}

		public static HandlerList getHandlerList() {
			return handlers;
		}
		////
		
		private Goal goal;
		private JailbreakPlayer jp;

		public GoalEvent(Goal goal, JailbreakPlayer jp) {
			this.goal = goal;
			this.jp = jp;
		}
		
		public Goal getGoal(){
			return goal;
		}

		public JailbreakPlayer getJailbreakPlayer() {
			return jp;
		}
	}
	
	public static class PlayerOnGoalEvent extends GoalEvent {
		public PlayerOnGoalEvent(Goal goal, JailbreakPlayer jp) {
			super(goal, jp);
		}
	}
	
	public static class PlayerOffGoalEvent extends GoalEvent {
		public PlayerOffGoalEvent(Goal goal, JailbreakPlayer jp) {
			super(goal, jp);
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
			if(isOnGoal(e.getTo()) && !isOnGoal(e.getFrom())){
				Bukkit.getServer().getPluginManager().callEvent(new PlayerOnGoalEvent(this, Jailbreak.getPlayer(e.getPlayer())));
			} else if (isOnGoal(e.getFrom()) && !isOnGoal(e.getTo())){
				Bukkit.getServer().getPluginManager().callEvent(new PlayerOffGoalEvent(this, Jailbreak.getPlayer(e.getPlayer())));
			}
		}
	}
	
	@EventHandler
	public void onDieOnGoal(JailbreakDeathEvent e) {
		if (isOnGoal(e.getJailbreakPlayer().getPlayer().getLocation())) {
			Bukkit.getServer().getPluginManager().callEvent(new PlayerOffGoalEvent(this, e.getJailbreakPlayer()));
		}
	}
	
}
