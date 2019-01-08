package me.cxom.jailbreak3.game;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.cxom.jailbreak3.Jailbreak;
import me.cxom.jailbreak3.player.PlayerProfile;

public class Lobby {
	
	private JailbreakGame game;
	Set<Player> waitingPlayers = new HashSet<>();
	
	public Lobby(JailbreakGame game) {
		this.game = game;
	}
	
	public void addPlayer(Player player){
		if (game.gamestate == GameState.STOPPED){
			player.sendMessage(Jailbreak.CHAT_PREFIX + ChatColor.RED + "This game has been stopped, you cannot join.");
			//TODO i18n
			return;
		}
		waitingPlayers.add(player);
		PlayerProfile.save(player);
		player.teleport(game.arena.getPregameLobby());
		player.setGameMode(GameMode.SURVIVAL);
		player.setFlying(false);
		player.setInvulnerable(true);
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setSaturation(20);
		player.setExhaustion(0);
		player.setFireTicks(0);
		player.getInventory().clear();
		if (game.gamestate == GameState.WAITING && waitingPlayers.size() >= game.arena.getPlayersToStart()){
			game.gamestate = GameState.STARTING;
			startCountdown();
		}
	}
	
	public void removePlayer(Player player){
		if (waitingPlayers.remove(player)){
			PlayerProfile.restore(player);
		}
	}
	
	public void removeAll(){
		waitingPlayers.forEach(PlayerProfile::restore);
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
					player.sendMessage(Jailbreak.CHAT_PREFIX + ChatColor.GOLD + "Match starting in " + i + " second(s) on " + game.arena.getName() + "!");
					player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, .25f, .9f);
				}
				i--;
			}
		}.runTaskTimerAsynchronously(Jailbreak.getPlugin(), 20, 20);
	}
	
	public void startNow(){
		if (game.gamestate != GameState.STARTING || waitingPlayers.size() < game.arena.getPlayersToStart()){
			waitingPlayers.forEach((p) -> p.sendMessage(Jailbreak.CHAT_PREFIX + ChatColor.RED + "Not enough players, start aborted!"));
			game.gamestate = GameState.WAITING;
		} else {
			game.start(waitingPlayers);
			waitingPlayers.clear();
		}
	}
	
}