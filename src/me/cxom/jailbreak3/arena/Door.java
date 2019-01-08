package me.cxom.jailbreak3.arena;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;

import me.cxom.jailbreak3.arena.region.Area;

public class Door implements Listener{

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
		for(Block b : door)
			b.setType(Material.IRON_BARS);
		open = false;
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
