package me.cxom.jailbreak3.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
import me.cxom.jailbreak3.gui.JailbreakGUI;
import me.cxom.jailbreak3.player.JailbreakPlayer;
import net.punchtree.minigames.MinigamesPlugin;
import net.punchtree.minigames.arena.Arena;
import net.punchtree.minigames.game.GameState;
import net.punchtree.minigames.game.PvpGame;
import net.punchtree.minigames.game.pvp.AttackMethod;
import net.punchtree.minigames.region.Area;
import net.punchtree.minigames.region.MultiRegion;
import net.punchtree.minigames.utility.FireworkUtils;
import net.punchtree.minigames.utility.player.InventoryUtils;

public class JailbreakGame implements PvpGame, Listener {

	private final JailbreakArena arena;
	private final Area allJails;
	
	private Set<JailbreakPlayer> players = new HashSet<>();
	private Set<JailbreakTeam> remaining = new HashSet<>();
	
	GameState gamestate = GameState.WAITING;

	private MovementSystem movement = MovementPlusPlus.CXOMS_MOVEMENT;

	private Consumer<Player> onPlayerLeaveGame;

	private BukkitTask free;
	
	///////////////////////////////////////////////////////////////
	
	private JailbreakGUI gui;
	
	public JailbreakGUI getGUI(){
		return gui;
	}
	
	///////////////////////////////////////////////////////////////
	
	public JailbreakGame(JailbreakArena arena){
		this.arena = arena;
		this.allJails = new MultiRegion(arena.getTeams().stream().map(t -> t.getJails()).collect(Collectors.toList()));
		this.gui = new JailbreakGUI(this);
		Bukkit.getServer().getPluginManager().registerEvents(this, Jailbreak.getPlugin());
	}
	
	public String getName() {
		return "Jailbreak";
	}
	
	public Location getPregameLobbySpawn() {
		return arena.getPregameLobby();
	}
	
	public int getPlayersNeededToStart() {
		return arena.getPlayersNeededToStart();
	}
	
	@Override
	public Arena getArena() {
		return arena;
	}
	
	@Override
	public GameState getGameState(){
		return gamestate;
	}
	
	public Set<JailbreakTeam> getRemainingTeams(){
		return remaining;
	}
	
	public boolean isInJail(Location location) {
		return allJails.contains(location);
	}

	@Override
	public void startGame(Set<Player> players, Consumer<Player> onPlayerLeaveGame){
		this.onPlayerLeaveGame = onPlayerLeaveGame;

		List<Player> playersList = new ArrayList<>(players);
		Collections.shuffle(playersList);
		List<JailbreakTeam> teams = arena.getTeams();
		int numTeams = teams.size();
		int i = 0;
		for(Player player : playersList){
			JailbreakTeam team = teams.get(i % numTeams);
			
			JailbreakPlayer jp = new JailbreakPlayer(player, team, this);
			Jailbreak.addPlayer(jp);
			team.addPlayer(jp);
			
			this.players.add(jp);
			player.teleport(team.getSpawns().get(i % team.getSpawns().size()));
			player.setInvulnerable(false);
			player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 1, 1.1f);
			gui.addPlayer(jp);
			player.getInventory().clear();
			InventoryUtils.equipPlayer(player, team.getColor());
			giveConcrete(player, team);
			movement.addPlayer(player);
			player.sendMessage(Jailbreak.CHAT_PREFIX + team.getChatColor() + "You are on the " + team.getName() + " Team!");
			i++;
			//TODO i18n
		}
		remaining.addAll(teams);
		free = new BukkitRunnable(){
			@Override
			public void run(){
				gui.update();
				arena.getTeams().forEach(JailbreakGame.this::checkForWin);
			};
		}.runTaskTimer(Jailbreak.getPlugin(), 20, 20);
		gui.update();
		gamestate = GameState.RUNNING;
	}
	
	private static ItemStack RED_TEAM = new ItemStack(Material.RED_CONCRETE);
	private static ItemStack BLUE_TEAM = new ItemStack(Material.BLUE_CONCRETE);
	static {
		ItemMeta rtmeta = RED_TEAM.getItemMeta();
		rtmeta.setDisplayName(ChatColor.DARK_RED + "Red Team");
		RED_TEAM.setItemMeta(rtmeta);
		
		ItemMeta btmeta = BLUE_TEAM.getItemMeta();
		btmeta.setDisplayName(ChatColor.DARK_BLUE + "Blue Team");
		BLUE_TEAM.setItemMeta(btmeta);
	}
	private void giveConcrete(Player player, JailbreakTeam team) {
		if ( ! (team.getName().equalsIgnoreCase("Red") || team.getName().equalsIgnoreCase("Blue"))) { return; }
		if (team.getName().equalsIgnoreCase("Red")) {
			player.getInventory().setItem(2, RED_TEAM);
			player.getInventory().setItem(3, RED_TEAM);
			player.getInventory().setItem(4, RED_TEAM);
			player.getInventory().setItem(5, RED_TEAM);
			player.getInventory().setItem(6, RED_TEAM);
			player.getInventory().setItem(7, RED_TEAM);
			player.getInventory().setItem(8, RED_TEAM);
		} else {
			player.getInventory().setItem(2, BLUE_TEAM);
			player.getInventory().setItem(3, BLUE_TEAM);
			player.getInventory().setItem(4, BLUE_TEAM);
			player.getInventory().setItem(5, BLUE_TEAM);
			player.getInventory().setItem(6, BLUE_TEAM);
			player.getInventory().setItem(7, BLUE_TEAM);
			player.getInventory().setItem(8, BLUE_TEAM);
		}
	}
	
	private void respawnPlayer(JailbreakPlayer jp){
		Player player = jp.getPlayer();
		player.setHealth(20);
		player.setFireTicks(0);
		// TODO circulating list?
		List<Location> jailspawns = jp.getTeam().getJailspawns(); 
		player.teleport(jailspawns.get((int) (Math.random() * jailspawns.size())));
	}
	
	public void removePlayer(Player player){
		if (Jailbreak.isPlayer(player)){ 
			JailbreakPlayer jp = Jailbreak.getPlayer(player);
			
			if (players.contains(jp)){
				JailbreakTeam team = jp.getTeam();
				
				gui.removePlayer(jp.getPlayer());
				gui.update();
				jp.getTeam().removePlayer(jp);
				players.remove(jp);
				
				checkForWin(team);
				if (gamestate == GameState.RUNNING && team.getAlive() == 1) {
					gui.playLastMemberAlive(players.stream().filter(p -> p.getTeam() == team && p.isFree()).findFirst().get());
				}
				
				Jailbreak.removePlayer(player);
				onPlayerLeaveGame.accept(player);
			}
		}
	}
	
	public void checkForWin(JailbreakTeam team){
		if (team.getAlive() == 0){
			remaining.remove(team);
		}
		if (gamestate == GameState.ENDING) {
			return;
		}
		if (remaining.size() == 1){
			gamestate = GameState.ENDING;
			JailbreakTeam winner = remaining.iterator().next(); 
			for(JailbreakPlayer jp : players){
				Player player = jp.getPlayer();
				JailbreakTeam jt = jp.getTeam();
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
		for(JailbreakPlayer jp : players){
			Jailbreak.removePlayer(jp.getPlayer());
			movement.removePlayer(jp.getPlayer());
			onPlayerLeaveGame.accept(jp.getPlayer());
		}
		gui.removeAll();
		players.clear();
		remaining.clear();
		arena.getTeams().forEach(JailbreakTeam::reset);
		gamestate = GameState.WAITING;
	}
	
	@EventHandler
	public void onWalkOnGoal(PlayerOnGoalEvent e){
		if (players.contains(e.getJailbreakPlayer())){
			JailbreakTeam team = e.getJailbreakPlayer().getTeam();
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
		if (players.contains(e.getJailbreakPlayer())){
			JailbreakTeam team = e.getJailbreakPlayer().getTeam();
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
		if (players.contains(jp)){
			
			Player killer = null;
			EntityDamageByEntityEvent edbee = null;
			
			if (e.getEntityDamageEvent() instanceof EntityDamageByEntityEvent) {
				edbee = (EntityDamageByEntityEvent) e.getEntityDamageEvent();
				Entity killingEntity = edbee.getDamager();
				
				// Determine killer
				if (killingEntity instanceof Player && Jailbreak.isPlayer(killingEntity.getUniqueId())){
					//Killed by a player (also in game)
					killer = (Player) killingEntity;	
					
				} else if (killingEntity instanceof Arrow && ((Arrow) killingEntity).getShooter() instanceof Player){
					//Killed by an arrow shot by a player (not sure if player in game yet)
					
					Player shooter = (Player) ((Arrow) killingEntity).getShooter();
					killingEntity.remove();
					
					if (Jailbreak.isPlayer(shooter.getUniqueId())){
						//Player who shot arrow is in game
						killer = shooter;
						
					}
				}
				
				if (killer != null) {
					gui.sendKill(Jailbreak.getPlayer(killer), jp, AttackMethod.getAttackMethod(edbee.getDamager()));
				}
			}
			
			e.getEntityDamageEvent().setCancelled(true);
			new BukkitRunnable() {
				public void run() {
					respawnPlayer(jp);
					JailbreakTeam team = jp.getTeam();
					gui.update();
					checkForWin(team);
					if (gamestate == GameState.RUNNING && team.getAlive() == 1) {
						gui.playLastMemberAlive(players.stream().filter(player -> player.getTeam() == team && player.isFree()).findFirst().get());
					}
				}
			}.runTaskLater(MinigamesPlugin.getInstance(), 1);
		}
	}
	
	@EventHandler
	public void onPlayerLeaveGame(PlayerCommandPreprocessEvent e){
		if (e.getMessage().equalsIgnoreCase("/leave") && hasPlayer(e.getPlayer())){
			removePlayer(e.getPlayer());
		}
	}
	
	private boolean hasPlayer(Player player) { return hasPlayer(player.getUniqueId()); }
	private boolean hasPlayer(UUID uniqueId) {
		JailbreakPlayer jp = Jailbreak.getPlayer(uniqueId);
		return (jp != null && players.contains(jp));
	}
	
	@EventHandler
	public void onPlayerQuitServer(PlayerQuitEvent e){
		if (hasPlayer(e.getPlayer())) {
			removePlayer(e.getPlayer());
		}
	}
	
	public void interruptAndShutdown(){
		end();
		gamestate = GameState.STOPPED;
	}
	
//	public void allowStart(){
//		gamestate = GameState.WAITING;
//	}
	
}
