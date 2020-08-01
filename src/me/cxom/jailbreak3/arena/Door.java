package me.cxom.jailbreak3.arena;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.event.Listener;

import net.punchtree.minigames.region.Area;

public class Door implements Listener{

	private static final BlockFace[] SIDE_FACES = { 
			BlockFace.WEST,
			BlockFace.NORTH,
			BlockFace.EAST, 
			BlockFace.SOUTH };
	
	private final Area door;
	
	private boolean open;
	
	public Door(Area door){
		this.door = door;
	}
	
	public void open(){
		if (open) return;
		for(Block b : door)
			b.setType(Material.AIR);
		open = true;
	}
	
	public void close(){
		if (!open) return;
		for(Block b : door) {
			b.setType(Material.IRON_BARS);
			MultipleFacing multiFacing = (MultipleFacing) b.getBlockData();
			for (BlockFace face : SIDE_FACES) {
				Material relative = b.getRelative(face).getType();
				if (relative.isSolid() && !isSlab(relative)) {
					multiFacing.setFace(face, true);
				}
			}
			b.setBlockData(multiFacing);
		}
		open = false;
	}
	
	private boolean isSlab(Material material) {
		// This is such jank but if it works...
		return material.name().contains("SLAB");
	}
	
	public boolean isOpen(){
		return open;
	}

	public void trigger() {
		if (isOpen())
			close();
		else
			open();
	}
	
}
