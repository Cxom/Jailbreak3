package me.cxom.jailbreak3.gui;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

import me.cxom.jailbreak3.Jailbreak;
import me.cxom.jailbreak3.arena.JailbreakTeam;
import me.cxom.jailbreak3.game.JailbreakGame;
import me.cxom.jailbreak3.player.JailbreakPlayer;
import net.punchtree.minigames.game.pvp.AttackMethod;
import net.punchtree.minigames.gui.Killfeed;

public class JailbreakGUI {

	private BossBar bossbar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SEGMENTED_10);
	private Killfeed killfeed = new Killfeed(Jailbreak.CHAT_PREFIX);
	private JailbreakTabList tablist = new JailbreakTabList();
	
	private JailbreakGame game;
	
	public JailbreakGUI(JailbreakGame game) {
		this.game = game;
	}
	
	public void addPlayer(JailbreakPlayer player){
		bossbar.addPlayer(player.getPlayer());
		killfeed.addPlayer(player.getPlayer());
		tablist.addPlayer(player);
	}
	
	public void removePlayer(JailbreakPlayer player){
		bossbar.removePlayer(player.getPlayer());
		killfeed.removePlayer(player.getPlayer());
		tablist.removePlayer(player.getPlayer());
	}
	
	public void removeAll(){
		bossbar.removeAll();
		killfeed.removeAll();
		tablist.removeAll();
	}
	
	public void update(){
		String bar = "";
		String actionbar = "";
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
		}
		bossbar.setTitle(bar.substring(0, Math.max(0, bar.length() - 3)));
		sendActionBar(actionbar);
	}
	
	public void playLastMemberAlive(JailbreakPlayer jp) {
		ActionBarAPI.sendActionBar(jp.getPlayer(), ChatColor.GRAY + "You're the last player alive on your team!");
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
		for (JailbreakPlayer jp: game.getPlayers().keySet()){
			ActionBarAPI.sendActionBar(jp.getPlayer(), message);
		}
	}
	
	public void sendKill(JailbreakPlayer killer, JailbreakPlayer killed, AttackMethod attackMethod) {
		killfeed.sendKill(killer, killed, attackMethod);
	}
	
}
