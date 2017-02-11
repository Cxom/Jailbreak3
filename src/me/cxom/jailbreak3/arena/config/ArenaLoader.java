package me.cxom.jailbreak3.arena.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import me.cxom.jailbreak3.arena.Door;
import me.cxom.jailbreak3.arena.Goal;
import me.cxom.jailbreak3.arena.JailbreakArena;
import me.cxom.jailbreak3.arena.JailbreakTeam;
import me.cxom.jailbreak3.arena.region.Area;
import me.cxom.jailbreak3.arena.region.MultiRegion;
import me.cxom.jailbreak3.arena.region.Region;
import me.cxom.jailbreak3.utils.JailbreakColor;

public class ArenaLoader {
	
	public static JailbreakArena load(FileConfiguration arenacfg){
		String name = arenacfg.getString("name");
		
		List<JailbreakTeam> teams = getList(arenacfg.getConfigurationSection("teams"),
											ArenaLoader::getTeam);
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

		JailbreakColor color = getColor(section.getConfigurationSection("color"));
		if (name == null && color == null) return null;
		if (name == null){
			name = StringUtils.capitalize(color.getChatColor().name());
		} else if (color == null){
			color = JailbreakColor.valueOf(name); //Defaults to white
		}
		
		World world = getRootWorld(section);
		if (world == null) return null;
		
		List<Location> spawns = ArenaLoader.getList(section.getConfigurationSection("spawns"),
				(ConfigurationSection spawn) -> { return ArenaLoader.getLocation(spawn, world); });
		if (spawns.isEmpty()) return null;

		Location goalL = getLocation(section.getConfigurationSection("goal.location"), world);
		if (goalL == null) return null;
		Double radius = section.getDouble("goal.radius", 2.5);
		List<Region> doorRegions = getRegionList(section.getConfigurationSection("doors"));
		if (doorRegions.isEmpty()) return null;
		Door door = new Door(new MultiRegion(doorRegions));
		Goal goal = new Goal(goalL, radius, door);
		
		List<Location> jailspawns = ArenaLoader.getList(section.getConfigurationSection("jailspawns"),
				(ConfigurationSection spawn) -> { return ArenaLoader.getLocation(spawn, world); });
		if (jailspawns.isEmpty()) return null;
		
		List<Region> jailRegions = getRegionList(section.getConfigurationSection("jails"));
		if (jailRegions.isEmpty()) return null;
		Area jails = new MultiRegion(jailRegions);
		
		return new JailbreakTeam(name, color, spawns, goal, jailspawns, jails);
	}
	
	/*Static loading parsers*/
	
	public static World getWorld(ConfigurationSection section){
		return Bukkit.getWorld(section.getString("world"));
	}
	
	public static World getRootWorld(ConfigurationSection section){
		return getWorld(section.getRoot());
	}
	
	public static String getName(File arenaFile){
		String name = arenaFile.getName();
        return name.substring(0, name.length() - 4);
	}
	
	public static JailbreakColor getColor(ConfigurationSection section){
		Integer red = section.getInt("red", -1);
		Integer green = section.getInt("green", -1);
		Integer blue = section.getInt("blue", -1);
		String chatcolor = section.getString("chatcolor"); 	
		if (red == null || green == null || blue == null){
			return null;
		}
		if (chatcolor == null){
			return new JailbreakColor(red, green, blue);
		} else {
			return new JailbreakColor(red, green, blue, ChatColor.valueOf(chatcolor));
		}
	}
	
	public static <T> List<T> getList(ConfigurationSection section, Function<ConfigurationSection, T> loader) throws ClassCastException{
		List<T> list = new ArrayList<>();
		for(String key : section.getKeys(false)){
			try {
				list.add(loader.apply(section.getConfigurationSection(key)));
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public static Location[] getLocationList(ConfigurationSection section) {
		Location[] list = new Location[section.getKeys(false).size()];
		int i = 0;
		for(String key : section.getKeys(false))
			list[i++] = (getLocation(section.getConfigurationSection(key)));
		return list;
	}
	
	public static Location getLocation(ConfigurationSection spawnInfo){
		return getLocation(spawnInfo, getRootWorld(spawnInfo));
	}
	
	public static Location getLocationWithWorld(ConfigurationSection section){
		return getLocation(section, getWorld(section));
	}
	
	public static Location getLocation(ConfigurationSection spawnInfo, World world){
		return new Location(
			world,
			spawnInfo.getDouble("x"),
			spawnInfo.getDouble("y"),
			spawnInfo.getDouble("z"),
			(float) spawnInfo.getDouble("pitch", 0),
			(float) spawnInfo.getDouble("yaw", 0) //TODO Is 0 pitch, 0 yaw looking straight forward?
		);
	}
	
	public static Region getRegion(ConfigurationSection regionInfo){
		return new Region(
			getLocation(regionInfo.getConfigurationSection("min")),
			getLocation(regionInfo.getConfigurationSection("max"))
		);
	}
	
	public static List<Region> getRegionList(ConfigurationSection section){
		return ArenaLoader.<Region>getList(section, ArenaLoader::getRegion);
	}
	
}
