package me.cxom.jailbreak3.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.cxom.jailbreak3.Jailbreak;
import me.cxom.jailbreak3.arena.Goal;
import me.cxom.jailbreak3.arena.Goal.PlayerOffGoalEvent;
import me.cxom.jailbreak3.arena.Goal.PlayerOnGoalEvent;
import me.cxom.jailbreak3.arena.JailbreakArena;
import me.cxom.jailbreak3.arena.JailbreakTeam;
import me.cxom.jailbreak3.events.custom.JailbreakDeathEvent;
import me.cxom.jailbreak3.player.JailbreakPlayer;
import me.cxom.jailbreak3.player.PlayerProfile;

public class GameInstance implements Listener {

	private final JailbreakArena arena;
	
	private Map<JailbreakPlayer, JailbreakTeam> players = new HashMap<>();
	private Map<JailbreakTeam, Integer> alive = new HashMap<>();
	
	private GameState gamestate = GameState.WAITING;
	
	private final BukkitRunnable free = new BukkitRunnable(){
		@Override
		public void run(){
			updateAliveStatuses();
		};
	};	
	
	public GameInstance(JailbreakArena arena){
		
		this.arena = arena;
		for(JailbreakTeam team : arena.getTeams()){
			alive.put(team, 0);
		}
		Bukkit.getServer().getPluginManager().registerEvents(this, Jailbreak.getPlugin());
	}
	
	public GameState getGameState(){
		return gamestate;
	}
	
	private void start(Set<Player> players){
		gamestate = GameState.STARTING;
		List<JailbreakTeam> teams = arena.getTeams();
		int numTeams = teams.size();
		int i = 0;
		for(Player player : players){
			JailbreakPlayer jp = new JailbreakPlayer(player);
			Jailbreak.addPlayer(jp);
			JailbreakTeam team = teams.get(i % numTeams);
			this.players.put(jp, team);
			this.alive.put(team, alive.get(team) + 1);
			player.teleport(team.getSpawns().get(i % team.getSpawns().size()));
			i++;
			player.sendMessage(Jailbreak.CHAT_PREFIX + team.getChatColor() + "You are on the " + team.getName() + " Team!");
			//TODO i18n
		}
		free.runTaskTimer(Jailbreak.getPlugin(), 20, 20);
		updateGUI();
		gamestate = GameState.RUNNING;
	}
	
	private void addAlive(JailbreakTeam team){
		alive.put(team, alive.get(team) + 1);
	}
	
	private void removeAlive(JailbreakTeam team){
		alive.put(team, alive.get(team) - 1);
	}
	
	private void respawnPlayer(JailbreakPlayer jp){
		jp.setFree(false);
		Player player = jp.getPlayer();
		player.setHealth(20);
		player.setFireTicks(0);
		JailbreakTeam team = players.get(jp);
		List<Location> jailspawns = team.getJailspawns(); 
		player.teleport(jailspawns.get((int) (Math.random() * jailspawns.size())));
	}
	
	private void updateAliveStatuses(){
		for(Map.Entry<JailbreakPlayer, JailbreakTeam> jpjt : players.entrySet()){
			JailbreakPlayer jp = jpjt.getKey();
			if (!jp.isFree() && !jpjt.getValue().getJails().contains(jp.getPlayer().getLocation())){
				jp.setFree(true);
				addAlive(players.get(jp));
				updateGUI();
			}
		}
	}
	
	private void updateGUI(){
		
	}
	
	public void checkForWin(JailbreakTeam team){
		if (alive.get(team) <= 0){
			updateAliveStatuses();
		}
		if (alive.get(team) <= 0){
			alive.remove(team);
		}
		if (alive.size() == 1){
			JailbreakTeam winner = alive.keySet().iterator().next(); 
			for(Map.Entry<JailbreakPlayer, JailbreakTeam> jpjt : players.entrySet()){
				Player player = jpjt.getKey().getPlayer();
				JailbreakTeam jt = jpjt.getValue();
				if (jt.equals(winner)){
					player.sendMessage(Jailbreak.CHAT_PREFIX + ChatColor.GREEN + "Your team won! :D");
					//TODO i18n
				} else {
					player.sendMessage(Jailbreak.CHAT_PREFIX + ChatColor.RED + "Your team lost. :/");
				}
			}
			end();
		}
	}
	
	private void end(){
		free.cancel();
		for(JailbreakPlayer jp : players.keySet()){
			PlayerProfile.restore(jp.getPlayer());
			Jailbreak.removePlayer(jp.getPlayer());
		}
		players.clear();
		alive.clear();
		for(JailbreakTeam team : arena.getTeams()){
			alive.put(team, 0);
			team.getGoal().setActive(0);
			team.getGoal().setDefended(0);
		}
		gamestate = GameState.WAITING;
	}
	
	@EventHandler
	public void onWalkOnGoal(PlayerOnGoalEvent e){
		System.out.println("Goal event: " + arena.getName());
		if (players.containsKey(e.getJailbreakPlayer())){
			JailbreakTeam team = players.get(e.getJailbreakPlayer());
			Goal goal = e.getGoal();
			if (goal.equals(team.getGoal())){
				goal.addActive();
				if (!goal.isDefended()){
					team.getDoor().open();
				}
			} else {
				goal.addDefended();
				team.getDoor().close();
			}
		}
	}
	
	@EventHandler
	public void onWalkOffGoal(PlayerOffGoalEvent e){
		if (players.containsKey(e.getJailbreakPlayer())){
			JailbreakTeam team = players.get(e.getJailbreakPlayer());
			Goal goal = e.getGoal();
			if (e.getGoal().equals(team.getGoal())){
				goal.removeActive();
				if (!goal.isActive()){
					team.getDoor().close();
				}
			} else {
				goal.removeDefended();
				if (goal.isActive()){
					team.getDoor().open();
				}
			}
		}		
	}
	
	@EventHandler
	public void onJailbreakDeath(JailbreakDeathEvent e){
		JailbreakPlayer jp = e.getJailbreakPlayer();
		if (players.containsKey(jp)){
			e.getEntityDamageEvent().setCancelled(true);
			respawnPlayer(jp);
			JailbreakTeam team = players.get(jp);
			removeAlive(team);
			updateGUI();
			checkForWin(team);
		}
	}	
	
	@EventHandler
	public void onPlayerQuitServer(PlayerQuitEvent e){
		Player player = e.getPlayer();
		if (Jailbreak.isPlayer(player)){ 
			JailbreakPlayer jp = Jailbreak.getPlayer(player);
			if (players.containsKey(jp)){
				removeAlive(players.get(jp));
				updateGUI();
				checkForWin(players.get(jp));
				players.remove(jp);
			}
			Jailbreak.removePlayer(player);
			PlayerProfile.restore(player);
		}
	}
	
	public void forceStop(){
		end();
		gamestate = GameState.STOPPED;
	}
	
	public void allowStart(){
		gamestate = GameState.WAITING;
	}
	
}
