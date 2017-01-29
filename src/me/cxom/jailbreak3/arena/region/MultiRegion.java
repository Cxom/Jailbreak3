package me.cxom.jailbreak3.arena.region;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.google.common.collect.Iterators;

public class MultiRegion implements Area{

	private final List<Region> regions;
	
	public MultiRegion(List<Region> regions){
		this.regions = regions;
	}

	@Override
	public boolean contains(Location l){
		for(Region r : regions)
			if (r.contains(l)) return true;
		return false;
	}
	
	@Override
	public Iterator<Block> iterator(){
		Iterator<Block> it = Iterators.emptyIterator();
		for(Region r : regions)
			it = Iterators.concat(it, r.iterator());
		return it;
	}
	
}
