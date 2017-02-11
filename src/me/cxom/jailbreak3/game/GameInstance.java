package me.cxom.jailbreak3.game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.cxom.jailbreak3.Jailbreak;
import me.cxom.jailbreak3.arena.JailbreakArena;
import me.cxom.jailbreak3.arena.JailbreakTeam;
import me.cxom.jailbreak3.events.custom.JailbreakDeathEvent;
import me.cxom.jailbreak3.player.JailbreakPlayer;
import me.cxom.jailbreak3.player.PlayerProfile;
import me.cxom.jailbreak3.utils.InventoryUtils;

public class GameInstance implements Listener {

	private final JailbreakArena arena;
	
	private Map<JailbreakPlayer, JailbreakTeam> players = new HashMap<>();
	private Map<JailbreakTeam, Integer> alive = new HashMap<>();
	
	private GameState gamestate = GameState.WAITING;
	
	private Lobby lobby = new Lobby();
	
	public class Lobby {
		
		Set<Player> waitingPlayers = new HashSet<>();
		
		public void addPlayer(Player player){
			if (gamestate == GameState.STOPPED){
				player.sendMessage(Jailbreak.CHAT_PREFIX + ChatColor.RED + "This game has been stopped, you cannot join.");
				//TODO i18n
				return;
			}
			waitingPlayers.add(player);
			PlayerProfile.save(player);
			player.teleport(arena.getPregameLobby());
			player.setGameMode(GameMode.SURVIVAL);
			player.setFlying(false);
			//TODO Hunger, health, saturation, xp
			player.getInventory().clear();
			if (gamestate == GameState.WAITING && waitingPlayers.size() >= arena.getPlayersToStart()){
				gamestate = GameState.STARTING;
				startCountdown();
			}
		}
		
		public void removePlayer(Player player){
			waitingPlayers.remove(player);
			PlayerProfile.restore(player);
		}
		
		public void removeAll(){
			for (Player player : waitingPlayers){
				PlayerProfile.restore(player);
			}
			waitingPlayers.clear();
		}
		
		public Set<Player> getWaitingPlayers(){
			return waitingPlayers;
		}
		
		public void startCountdown(){
			new BukkitRunnable(){
				int i = 10;
				@Override
				public void run(){
					if (i <= 0){
						this.cancel();
						startNow();
						return;
					}
					for (Player player : waitingPlayers){
						player.sendMessage(Jailbreak.CHAT_PREFIX + ChatColor.GOLD + "Match starting in " + i + " second(s) on " + arena.getName() + "!");
					}
					i--;
				}
			}.runTaskTimerAsynchronously(Jailbreak.getPlugin(), 20, 20);
		}
		
		public void startNow(){
			if (gamestate != GameState.STARTING || waitingPlayers.size() < arena.getPlayersToStart()){
				for (Player player : waitingPlayers){
					player.sendMessage(Jailbreak.CHAT_PREFIX + ChatColor.RED + "Not enough players, start aborted!");
				}
				gamestate = GameState.WAITING;
			} else {
				start(waitingPlayers);
				waitingPlayers.clear();
			}
		}
		
	}
	
	private BukkitTask free;
	
	public GameInstance(JailbreakArena arena){
		
		this.arena = arena;
		for(JailbreakTeam team : arena.getTeams()){
			team.setPlayerSupplier(() -> {return players;});
			alive.put(team, 0);
		}
		Bukkit.getServer().getPluginManager().registerEvents(this, Jailbreak.getPlugin());
	}
	
	public GameState getGameState(){
		return gamestate;
	}
	
	public Lobby getLobby(){
		return lobby;
	}
	
	private void start(Set<Player> players){
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
			InventoryUtils.equipPlayer(player, team.getColor());
			player.sendMessage(Jailbreak.CHAT_PREFIX + team.getChatColor() + "You are on the " + team.getName() + " Team!");
			i++;
			//TODO i18n
		}
		free = new BukkitRunnable(){
			@Override
			public void run(){
				updateAliveStatuses();
			};
		}.runTaskTimer(Jailbreak.getPlugin(), 20, 20);
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
		} else {
			lobby.removePlayer(e.getPlayer());
			// for performance reasons, does checking if a player is in the lobby even matter?
		}
	}
	
	public void forceStop(){
		end();
		lobby.removeAll();
		gamestate = GameState.STOPPED;
	}
	
	public void allowStart(){
		gamestate = GameState.WAITING;
	}
	
}
