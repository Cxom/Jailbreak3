package me.cxom.jailbreak3.arena.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import me.cxom.jailbreak3.arena.Goal;
import me.cxom.jailbreak3.arena.JailbreakArena;
import me.cxom.jailbreak3.arena.JailbreakTeam;
import net.punchtree.minigames.arena.Door;
import net.punchtree.minigames.arena.creation.ArenaLoader;
import net.punchtree.minigames.region.Area;
import net.punchtree.minigames.region.MultiRegion;
import net.punchtree.minigames.region.Region;
import net.punchtree.util.color.PunchTreeColor;

public class JailbreakArenaLoader extends ArenaLoader {
	
	private static final Material DEFAULT_DOOR_MATERIAL = Material.IRON_BARS;
	
	public static JailbreakArena load(FileConfiguration arenacfg){
		String name = arenacfg.getString("name");
		
		World world = getRootWorld(arenacfg);
		Location relative = new Location(world, 0, 0, 0);
		
		boolean isRelative = arenacfg.isConfigurationSection("relative");
		if (isRelative){
			relative = getLocation(arenacfg.getConfigurationSection("relative"));
		}
		
		final Location finalRelative = relative;
		
		List<JailbreakTeam> teams = getList(arenacfg.getConfigurationSection("teams"),
											configSec -> JailbreakArenaLoader.getTeam(configSec, finalRelative));
		
		
		//^^ Make sure no duplicate names can be saved when creating teams
		teams.removeAll(Collections.singleton(null));
		if (teams.size() < 2) return null;
		
		Location pregameLobby;
		if (arenacfg.isConfigurationSection("lobby")){
			pregameLobby = getRelativeLocation(arenacfg.getConfigurationSection("lobby"), relative);
		} else {
			pregameLobby = teams.get(0).getSpawns().get(0);
		}
		
		int playersToStart = arenacfg.getInt("playersToStart", teams.size());
		
		return new JailbreakArena(name, pregameLobby, playersToStart, teams);
	};
	
	public static JailbreakTeam getTeam(ConfigurationSection section, Location relative){
		String name = section.getString("name");

		PunchTreeColor color = getColor(section.getConfigurationSection("color"));
		if (name == null && color == null) return null;
		if (name == null){
			String chatColorName = color.getChatColor().name().replace('_', ' ');
			name = chatColorName.substring(0, 1).toUpperCase() + chatColorName.substring(1).toLowerCase();
		} else if (color == null){
			color = PunchTreeColor.valueOf(name); //Defaults to white
		}
		
		World world = getRootWorld(section);
		if (world == null) return null;
		
		List<Location> spawns = Arrays.asList(getRelativeLocationList(section.getConfigurationSection("spawns"), relative));
		if (spawns.isEmpty()) return null;

		Location goalL = getRelativeLocation(section.getConfigurationSection("goal.location"), relative);
		if (goalL == null) return null;
		Double radius = section.getDouble("goal.radius", 2.5);
		List<Region> doorRegions = getRelativeRegionList(section.getConfigurationSection("doors"), relative);
		if (doorRegions.isEmpty()) return null;
		String doorMaterialStr = section.getString("doormaterial");
		Material doorMaterial = DEFAULT_DOOR_MATERIAL;
		try {
			if (doorMaterialStr != null) {
				doorMaterial = Material.valueOf(doorMaterialStr);
			}
		} catch (IllegalArgumentException iaex) {
			Bukkit.getLogger().severe("Error loading door material: '" + doorMaterialStr + "' is not a valid material");
		}
		Door door = new Door(new MultiRegion(doorRegions), doorMaterial);
		Goal goal = new Goal(goalL, radius, door);
		
		List<Location> jailspawns = Arrays.asList(getRelativeLocationList(section.getConfigurationSection("jailspawns"), relative));
		if (jailspawns.isEmpty()) return null;
		
		List<Region> jailRegions = getRelativeRegionList(section.getConfigurationSection("jails"), relative);
		if (jailRegions.isEmpty()) return null;
		Area jails = new MultiRegion(jailRegions);
		
		return new JailbreakTeam(name, color, spawns, goal, jailspawns, jails);
	}
	
}
