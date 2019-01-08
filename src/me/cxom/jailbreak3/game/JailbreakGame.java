package me.cxom.jailbreak3.game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.trinoxtion.movement.MovementPlusPlus;
import com.trinoxtion.movement.MovementSystem;

import me.cxom.jailbreak3.Jailbreak;
import me.cxom.jailbreak3.arena.Goal;
import me.cxom.jailbreak3.arena.Goal.PlayerOffGoalEvent;
import me.cxom.jailbreak3.arena.Goal.PlayerOnGoalEvent;
import me.cxom.jailbreak3.arena.JailbreakArena;
import me.cxom.jailbreak3.arena.JailbreakTeam;
import me.cxom.jailbreak3.events.custom.JailbreakDeathEvent;
import me.cxom.jailbreak3.game.lobby.Lobby;
import me.cxom.jailbreak3.gui.JailbreakGUI;
import me.cxom.jailbreak3.player.JailbreakPlayer;
import me.cxom.jailbreak3.player.PlayerProfile;
import me.cxom.jailbreak3.utils.FireworkUtils;
import me.cxom.jailbreak3.utils.InventoryUtils;

public class JailbreakGame implements PvpGame, Listener {

	final JailbreakArena arena;
	
	private Map<JailbreakPlayer, JailbreakTeam> players = new HashMap<>();
	private Set<JailbreakTeam> remaining = new HashSet<>();
	
	GameState gamestate = GameState.WAITING;
	
	private MovementSystem movement = MovementPlusPlus.CXOMS_MOVEMENT;
	
	private BukkitTask free;
	
	///////////////////////////////////////////////////////////////
	
	private Lobby lobby;
	
	public Lobby getLobby(){
		return lobby;
	}
	
	///////////////////////////////////////////////////////////////
	
	private JailbreakGUI gui;
	
	public JailbreakGUI getGUI(){
		return gui;
	}
	
	///////////////////////////////////////////////////////////////
	
	public JailbreakGame(JailbreakArena arena){
		this.arena = arena;
		this.lobby = new Lobby(this, this::start);
		this.gui = new JailbreakGUI(this);
		Bukkit.getServer().getPluginManager().registerEvents(this, Jailbreak.getPlugin());
	}
	
	public String getName() {
		return arena.getName();
	}
	
	public Location getPregameLobbySpawn() {
		return arena.getPregameLobby();
	}
	
	public int getPlayersNeededToStart() {
		return arena.getPlayersToStart();
	}
	
	public GameState getGameState(){
		return gamestate;
	}
	
	public Map<JailbreakPlayer, JailbreakTeam> getPlayers(){
		return players;
	}
	
	public Set<JailbreakTeam> getRemainingTeams(){
		return remaining;
	}
	
	void start(Set<Player> players){
		List<JailbreakTeam> teams = arena.getTeams();
		int numTeams = teams.size();
		int i = 0;
		for(Player player : players){
			JailbreakPlayer jp = new JailbreakPlayer(player);
			Jailbreak.addPlayer(jp);
			JailbreakTeam team = teams.get(i % numTeams);
			this.players.put(jp, team);
			team.incrementSize();
			team.incrementAlive();
			player.teleport(team.getSpawns().get(i % team.getSpawns().size()));
			player.setInvulnerable(false);
			player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 1, 1.1f);
			gui.addPlayer(player);
			InventoryUtils.equipPlayer(player, team.getColor());
			movement.addPlayer(player);
			player.sendMessage(Jailbreak.CHAT_PREFIX + team.getChatColor() + "You are on the " + team.getName() + " Team!");
			i++;
			//TODO i18n
		}
		remaining.addAll(teams);
		free = new BukkitRunnable(){
			@Override
			public void run(){
				updateAliveStatuses();
			};
		}.runTaskTimer(Jailbreak.getPlugin(), 20, 20);
		gui.update();
		gamestate = GameState.RUNNING;
	}
	
	private void respawnPlayer(JailbreakPlayer jp){
		jp.setFree(false);
		Player player = jp.getPlayer();
		player.setHealth(20);
		player.setFireTicks(0);
		List<Location> jailspawns = players.get(jp).getJailspawns(); 
		player.teleport(jailspawns.get((int) (Math.random() * jailspawns.size())));
	}
	
	public void removePlayer(Player player){
		if (Jailbreak.isPlayer(player)){ 
			JailbreakPlayer jp = Jailbreak.getPlayer(player);
			
			if (players.containsKey(jp)){
				JailbreakTeam team = players.get(jp);
				team.decrementSize();
				if (jp.isFree()){
					team.decrementAlive();
				}
				
				gui.removePlayer(player);
				gui.update();
				
				checkForWin(team);
				players.remove(jp);
				Jailbreak.removePlayer(player);
				PlayerProfile.restore(player);
			}
		} else {
			lobby.removeAndRestorePlayer(player);
		}
	}
	
	private void updateAliveStatuses(){
		for(Map.Entry<JailbreakPlayer, JailbreakTeam> jpjt : players.entrySet()){
			JailbreakPlayer jplayer = jpjt.getKey();
			JailbreakTeam jteam = jpjt.getValue();
			if (!jplayer.isFree() && !jteam.getJails().contains(jplayer.getPlayer().getLocation())){
				jplayer.setFree(true);
				players.get(jplayer).incrementAlive();
				gui.update();
			}
		}
	}
	
	public void checkForWin(JailbreakTeam team){
		if (team.getAlive() <= 0){
			updateAliveStatuses();
		}
		if (team.getAlive() <= 0){
			remaining.remove(team);
		}
		if (remaining.size() == 1){
			JailbreakTeam winner = remaining.iterator().next(); 
			for(Map.Entry<JailbreakPlayer, JailbreakTeam> jpjt : players.entrySet()){
				Player player = jpjt.getKey().getPlayer();
				JailbreakTeam jt = jpjt.getValue();
				if (jt.equals(winner)){
					player.sendMessage(Jailbreak.CHAT_PREFIX + ChatColor.GREEN + "Your team won! :D");
					//NOT the way to do this
					Random r = new Random();
					FireworkUtils.spawnFirework(player.getLocation(), jt.getColor(), r.nextInt(3));
					//TODO i18n
				} else {
					player.sendMessage(Jailbreak.CHAT_PREFIX + ChatColor.RED + "Your team lost. :/");
				}
				player.setGameMode(GameMode.ADVENTURE);
				player.setAllowFlight(true);
				player.setFlying(true);
			}
			new BukkitRunnable(){
				@Override
				public void run(){
					end();
				}
			}.runTaskLater(Jailbreak.getPlugin(), 100);
		}
	}
	
	private void end(){
		if (free != null){
			free.cancel();
			free = null;
		}	
		for(JailbreakPlayer jp : players.keySet()){
			PlayerProfile.restore(jp.getPlayer());
			Jailbreak.removePlayer(jp.getPlayer());
			movement.removePlayer(jp.getPlayer());
		}
		gui.removeAll();
		players.clear();
		remaining.clear();
		for(JailbreakTeam team : arena.getTeams()){
			team.setSize(0);
			team.setAlive(0);
			team.getGoal().setActive(0);
			team.getGoal().setDefended(0);
			team.getGoal().getDoor().close();
		}
		gamestate = GameState.WAITING;
	}
	
	@EventHandler
	public void onWalkOnGoal(PlayerOnGoalEvent e){
		if (players.containsKey(e.getJailbreakPlayer())){
			JailbreakTeam team = players.get(e.getJailbreakPlayer());
			Goal goal = e.getGoal();
			if (goal.equals(team.getGoal())){
				goal.addActive();
				if (!goal.isDefended()){
					goal.getDoor().open();
				}
			} else {
				goal.addDefended();
				goal.getDoor().close();
			}
			gui.update();
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
					goal.getDoor().close();
				}
			} else {
				goal.removeDefended();
				if (goal.isActive()){
					goal.getDoor().open();
				}
			}
			gui.update();
		}
	}
	
	@EventHandler
	public void onJailbreakDeath(JailbreakDeathEvent e){
		JailbreakPlayer jp = e.getJailbreakPlayer();
		if (players.containsKey(jp)){
			e.getEntityDamageEvent().setCancelled(true);
			respawnPlayer(jp);
			JailbreakTeam team = players.get(jp);
			team.decrementAlive();
			gui.update();
			checkForWin(team);
		}
	}	
	
	@EventHandler
	public void onPlayerLeaveGame(PlayerCommandPreprocessEvent e){
		if (e.getMessage().equalsIgnoreCase("/jailbreak leave")){
			removePlayer(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onPlayerQuitServer(PlayerQuitEvent e){
		removePlayer(e.getPlayer());
	}
	
	public void forceStop(){
		end();
		lobby.removeAndRestoreAll();
		gamestate = GameState.STOPPED;
	}
	
//	public void allowStart(){
//		gamestate = GameState.WAITING;
//	}
	
}
