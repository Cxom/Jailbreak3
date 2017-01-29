package me.cxom.jailbreak3.arena.region;

import org.bukkit.Location;
import org.bukkit.block.Block;

public interface Area extends Iterable<Block>{

	public boolean contains(Location l);
	
}
