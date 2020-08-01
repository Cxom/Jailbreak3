package me.cxom.jailbreak3.arena.config;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import me.cxom.jailbreak3.arena.Door;
import me.cxom.jailbreak3.arena.Goal;
import me.cxom.jailbreak3.arena.JailbreakArena;
import me.cxom.jailbreak3.arena.JailbreakTeam;
import net.punchtree.minigames.arena.creation.ArenaLoader;
import net.punchtree.minigames.region.Area;
import net.punchtree.minigames.region.MultiRegion;
import net.punchtree.minigames.region.Region;
import net.punchtree.minigames.utility.color.MinigameColor;

public class JailbreakArenaLoader extends ArenaLoader {
	
	public static JailbreakArena load(FileConfiguration arenacfg){
		String name = arenacfg.getString("name");
		
		List<JailbreakTeam> teams = getList(arenacfg.getConfigurationSection("teams"),
											JailbreakArenaLoader::getTeam);
		//^^ Make sure no duplicate names can be saved when creating teams
		teams.removeAll(Collections.singleton(null));
		if (teams.size() < 2) return null;
		
		Location pregameLobby;
		if (arenacfg.isConfigurationSection("lobby")){
			pregameLobby = getLocation(arenacfg.getConfigurationSection("lobby"));
		} else {
			pregameLobby = teams.get(0).getSpawns().get(0);
		}
		
		int playersToStart = arenacfg.getInt("playersToStart", teams.size());
		
		return new JailbreakArena(name, pregameLobby, playersToStart, teams);
	};
	
	public static JailbreakTeam getTeam(ConfigurationSection section){
		String name = section.getString("name");

		MinigameColor color = getColor(section.getConfigurationSection("color"));
		if (name == null && color == null) return null;
		if (name == null){
			name = StringUtils.capitalize(color.getChatColor().name());
		} else if (color == null){
			color = MinigameColor.valueOf(name); //Defaults to white
		}
		
		World world = getRootWorld(section);
		if (world == null) return null;
		
		List<Location> spawns = getList(section.getConfigurationSection("spawns"),
				(ConfigurationSection spawn) -> getLocation(spawn, world));
		if (spawns.isEmpty()) return null;

		Location goalL = getLocation(section.getConfigurationSection("goal.location"), world);
		if (goalL == null) return null;
		Double radius = section.getDouble("goal.radius", 2.5);
		List<Region> doorRegions = getRegionList(section.getConfigurationSection("doors"));
		if (doorRegions.isEmpty()) return null;
		Door door = new Door(new MultiRegion(doorRegions));
		Goal goal = new Goal(goalL, radius, door);
		
		List<Location> jailspawns = getList(section.getConfigurationSection("jailspawns"),
				(ConfigurationSection spawn) -> { return getLocation(spawn, world); });
		if (jailspawns.isEmpty()) return null;
		
		List<Region> jailRegions = getRegionList(section.getConfigurationSection("jails"));
		if (jailRegions.isEmpty()) return null;
		Area jails = new MultiRegion(jailRegions);
		
		return new JailbreakTeam(name, color, spawns, goal, jailspawns, jails);
	}
	
}
