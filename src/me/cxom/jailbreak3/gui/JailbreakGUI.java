package me.cxom.jailbreak3.gui;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import me.cxom.jailbreak3.Jailbreak;
import me.cxom.jailbreak3.arena.JailbreakTeam;
import me.cxom.jailbreak3.game.JailbreakGame;
import me.cxom.jailbreak3.player.JailbreakPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.punchtree.minigames.game.pvp.AttackMethod;
import net.punchtree.minigames.gui.Killfeed;

public class JailbreakGUI {

	private final JailbreakGame game;
	
	private final BossBar bossbar;
	private final Scoreboard scoreboard;
	private final Killfeed killfeed;
	private final JailbreakTabList tablist;
	
	private final Set<Player> players = new HashSet<>();
	
	public JailbreakGUI(JailbreakGame game) {
		this.game = game;
		this.bossbar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SEGMENTED_10);
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		this.killfeed = new Killfeed(scoreboard, Jailbreak.CHAT_PREFIX);
		this.tablist = new JailbreakTabList(scoreboard, "Playerlist test");
	}
	
	public void addPlayer(JailbreakPlayer jp){
		Player player = jp.getPlayer();
		
		players.add(player);
		player.setScoreboard(scoreboard);
		bossbar.addPlayer(player);
		tablist.addPlayer(jp);
	}
	
	public void removePlayer(Player player){
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		players.remove(player);
		bossbar.removePlayer(player);
		tablist.removePlayer(player);
	}
	
	public void removeAll(){
		players.forEach(player -> player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard()));
		players.clear();
		bossbar.removeAll();
		tablist.removeAll();
	}
	
	public void update(){
		String bar = "";
		String actionbar = "";
		boolean shouldSendActionBar = false;
		for (JailbreakTeam team : game.getRemainingTeams()){
			bar += team.getGoal().getDoor().isOpen() ? team.getChatColor() + "<OPEN>" : ChatColor.GRAY + "<>";
			bar += " " + team.getChatColor() + team.getName() + " ";
			bar += StringUtils.repeat("█", team.getAlive());
			bar += ChatColor.GRAY;
			bar += StringUtils.repeat("█", team.getSize() - team.getAlive());
			bar += ChatColor.WHITE + " | ";
			
			actionbar += ChatColor.UNDERLINE;
			actionbar += team.getGoal().getDoor().isOpen() ? team.getChatColor() + team.getName() + "'s door is open!" : 
					     team.getGoal().isDefended() ? team.getChatColor() + team.getName() + "'s door is being defended!" : "";
			actionbar += ChatColor.RESET + " ";
			
			if (team.getGoal().getDoor().isOpen() || team.getGoal().isDefended()) {
				shouldSendActionBar = true;
			}
		}
		bossbar.setTitle(bar.substring(0, Math.max(0, bar.length() - 3)));
		if (shouldSendActionBar) {
			sendActionBar(actionbar);
		}
	}
	
	public void playLastMemberAlive(JailbreakPlayer jp) {
		jp.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.LIGHT_PURPLE + "You're the last player alive on your team!"));
		new BukkitRunnable() {
			final Sound LAST_ALIVE_SOUND = Sound.ENTITY_ARROW_HIT_PLAYER;
			final int LAST_ALIVE_SOUNDS = 5;
			final Player player = jp.getPlayer();
			int soundCounter = 0;
			@Override
			public void run() {
				player.playSound(player.getLocation(), LAST_ALIVE_SOUND, 1, 2);
				if (soundCounter++ >= LAST_ALIVE_SOUNDS) {
					this.cancel();
				}
			}
		}.runTaskTimerAsynchronously(Jailbreak.getPlugin(), 0, 3);
	}
	
	public void sendActionBar(String message){
		for (Player player: players){
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
		}
	}
	
	public void sendKill(JailbreakPlayer killer, JailbreakPlayer killed, AttackMethod attackMethod) {
		killfeed.sendKill(killer, killed, attackMethod);
	}
	
}
