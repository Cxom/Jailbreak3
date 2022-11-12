package me.cxom.jailbreak3.gui;

import me.cxom.jailbreak3.arena.JailbreakTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.punchtree.util.debugvar.DebugVars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class GUITeamManager {

    private static final Component FRIEND_INDICATOR = Component.text(" ❤");
    private static final Component ENEMY_INDICATOR = Component.text(" ☠").color(TextColor.fromHexString(DebugVars.getString("jailbreak3.gui.enemy-indicator-color", "#DDDDDD")));

    private ScoreboardManager manager;
    private Scoreboard forTeam1;
    private Scoreboard forTeam2;

    private final Team team1ToTeam1, team1ToTeam2, team2ToTeam1, team2ToTeam2;

    public GUITeamManager(String gameName, JailbreakTeam team1, JailbreakTeam team2) {
        this.manager = Bukkit.getScoreboardManager();
        this.forTeam1 = manager.getNewScoreboard();
        this.forTeam2 = manager.getNewScoreboard();

        // TODO could this cause errors if we register a team name that already exists?
        team1ToTeam1 = initializeTeam(forTeam1, gameName + team1.getName() + "To" + team1.getName());
        team1ToTeam1.color(NamedTextColor.nearestTo(team1.getColor()));
        team1ToTeam1.suffix(FRIEND_INDICATOR.color(team1.getColor()));

        team1ToTeam2 = initializeTeam(forTeam2,gameName + team1.getName() + "To" + team2.getName());
        team1ToTeam2.color(NamedTextColor.nearestTo(team1.getColor()));
        team1ToTeam2.suffix(ENEMY_INDICATOR.colorIfAbsent(team1.getColor()));

        team2ToTeam1 = initializeTeam(forTeam1, gameName + team2.getName() + "To" + team1.getName());
        team2ToTeam1.color(NamedTextColor.nearestTo(team2.getColor()));
        team2ToTeam1.suffix(ENEMY_INDICATOR.colorIfAbsent(team2.getColor()));

        team2ToTeam2 = initializeTeam(forTeam2,gameName + team2.getName() + "To" + team2.getName());
        team2ToTeam2.color(NamedTextColor.nearestTo(team2.getColor()));
        team2ToTeam2.suffix(FRIEND_INDICATOR.color(team2.getColor()));
    }

    // XXX This asserts that no two games have the same name (such as two instances of the same arena) - this is not an enforced prerequisite!
    private Team initializeTeam(Scoreboard scoreboard, String teamName) {
//        Team team = scoreboard.getTeam(teamName);
//        if (team == null) {
//            team = scoreboard.registerNewTeam(teamName);
//        }
//        return team;
        return scoreboard.registerNewTeam(teamName);
    }

    public void addPlayerToTeam1(Player player) {
        team1ToTeam1.addPlayer(player);
        team1ToTeam2.addPlayer(player);
        player.setScoreboard(forTeam1);
    }

    public void addPlayerToTeam2(Player player) {
        team2ToTeam1.addPlayer(player);
        team2ToTeam2.addPlayer(player);
        player.setScoreboard(forTeam2);
    }

    public void removePlayer(Player player) {
        team1ToTeam1.removePlayer(player);
        team1ToTeam2.removePlayer(player);
        team2ToTeam1.removePlayer(player);
        team2ToTeam2.removePlayer(player);
        player.setScoreboard(manager.getMainScoreboard());
    }

    public void removeAll() {
        team1ToTeam1.removeEntries(team1ToTeam1.getEntries());
        team1ToTeam2.removeEntries(team1ToTeam2.getEntries());
        team2ToTeam1.removeEntries(team2ToTeam1.getEntries());
        team2ToTeam2.removeEntries(team2ToTeam2.getEntries());
        resetAllPlayersScoreboards();
    }

    public void cleanup() {
        team1ToTeam1.unregister();
        team1ToTeam2.unregister();
        team2ToTeam1.unregister();
        team2ToTeam2.unregister();
        resetAllPlayersScoreboards();
    }

    private void resetAllPlayersScoreboards() {
        forTeam1.getPlayers().stream()
                .filter(op -> op instanceof Player)
                .map(op -> (Player) op)
                .forEach(player -> player.setScoreboard(manager.getMainScoreboard()));
        forTeam2.getPlayers().stream()
                .filter(op -> op instanceof Player)
                .map(op -> (Player) op)
                .forEach(player -> player.setScoreboard(manager.getMainScoreboard()));
    }
}
