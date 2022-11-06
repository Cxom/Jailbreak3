package me.cxom.jailbreak3.gui;

import me.cxom.jailbreak3.Jailbreak;
import me.cxom.jailbreak3.arena.JailbreakTeam;
import me.cxom.jailbreak3.game.JailbreakGame;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class GUITeamManager {

    private static final String FRIEND_INDICATOR = "❤";
    private static final String ENEMY_INDICATOR = "⚔";

    private ScoreboardManager manager;
    private Scoreboard scoreboard;

    private final Team team1ToTeam1, team1ToTeam2, team2ToTeam1, team2ToTeam2;

    public GUITeamManager(String gameName, JailbreakTeam team1, JailbreakTeam team2) {
        this.manager = Bukkit.getScoreboardManager();
        this.scoreboard = manager.getMainScoreboard();

        // TODO could this cause errors if we register a team name that already exists?
        team1ToTeam1 = initializeTeam(gameName + team1.getName() + "To" + team1.getName());
        team1ToTeam1.color(NamedTextColor.nearestTo(team1.getColor()));
        team1ToTeam1.setPrefix(team1.getChatColor() + "");
        team1ToTeam1.setSuffix(team1.getChatColor() + FRIEND_INDICATOR);
        team1ToTeam1.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);

        team1ToTeam2 = initializeTeam(gameName + team1.getName() + "To" + team2.getName());
        team1ToTeam2.color(NamedTextColor.nearestTo(team1.getColor()));
        team1ToTeam2.setPrefix(team1.getChatColor() + "");
        team1ToTeam2.setSuffix(team1.getChatColor() + ENEMY_INDICATOR);
        team1ToTeam2.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);

        team2ToTeam1 = initializeTeam(gameName + team2.getName() + "To" + team1.getName());
        team2ToTeam1.color(NamedTextColor.nearestTo(team2.getColor()));
        team2ToTeam1.setPrefix(team2.getChatColor() + "");
        team2ToTeam1.setSuffix(team2.getChatColor() + ENEMY_INDICATOR);
        team2ToTeam1.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);

        team2ToTeam2 = initializeTeam(gameName + team2.getName() + "To" + team2.getName());
        team2ToTeam2.color(NamedTextColor.nearestTo(team2.getColor()));
        team2ToTeam2.setPrefix(team2.getChatColor() + "");
        team2ToTeam2.setSuffix(team2.getChatColor() + FRIEND_INDICATOR);
        team2ToTeam2.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
    }

    private Team initializeTeam(String teamName) {
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
    }

    public void addPlayerToTeam2(Player player) {
        team2ToTeam1.addPlayer(player);
        team2ToTeam2.addPlayer(player);
    }

    public void removePlayer(Player player) {
        team1ToTeam1.removePlayer(player);
        team1ToTeam2.removePlayer(player);
        team2ToTeam1.removePlayer(player);
        team2ToTeam2.removePlayer(player);
    }

    public void removeAll() {
        team1ToTeam1.removeEntries(team1ToTeam1.getEntries());
        team1ToTeam2.removeEntries(team1ToTeam2.getEntries());
        team2ToTeam1.removeEntries(team2ToTeam1.getEntries());
        team2ToTeam2.removeEntries(team2ToTeam2.getEntries());
    }

    public void cleanup() {
        team1ToTeam1.unregister();
        team1ToTeam2.unregister();
        team2ToTeam1.unregister();
        team2ToTeam2.unregister();
    }
}
