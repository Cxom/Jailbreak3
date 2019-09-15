package me.cxom.jailbreak3.gui;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

import me.cxom.jailbreak3.Jailbreak;
import me.cxom.jailbreak3.arena.JailbreakTeam;
import me.cxom.jailbreak3.game.AttackMethod;
import me.cxom.jailbreak3.game.JailbreakGame;
import me.cxom.jailbreak3.player.JailbreakPlayer;

public class JailbreakGUI {

	private BossBar bossbar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SEGMENTED_10);
	private Killfeed killfeed = new Killfeed(Jailbreak.CHAT_PREFIX);
	
	private JailbreakGame game;
	
	public JailbreakGUI(JailbreakGame game) {
		this.game = game;
	}
	
	public void addPlayer(Player player){
		bossbar.addPlayer(player);
		killfeed.addPlayer(player);
	}
	
	public void removePlayer(Player player){
		bossbar.removePlayer(player);
		killfeed.removePlayer(player);
	}
	
	public void removeAll(){
		bossbar.removeAll();
		killfeed.removeAll();
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
	
	public void sendActionBar(String message){
		for (JailbreakPlayer jp: game.getPlayers().keySet()){
			ActionBarAPI.sendActionBar(jp.getPlayer(), message);
		}
	}
	
	public void sendKill(JailbreakPlayer killer, JailbreakPlayer killed, AttackMethod attackMethod) {
		killfeed.sendKill(killer, killed, attackMethod);
	}
	
}
