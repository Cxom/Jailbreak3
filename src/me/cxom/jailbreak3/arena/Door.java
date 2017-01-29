package me.cxom.jailbreak3.arena;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.cxom.jailbreak3.Jailbreak;
import me.cxom.jailbreak3.JailbreakPlayer;
import me.cxom.jailbreak3.arena.region.Area;

public class Door implements Listener{

	private final Area door;
	private final Area out;
	
	private final Consumer<JailbreakPlayer> doorTraverse;
	
	private boolean open;
	
	public Door(Area door, Area out, Consumer<JailbreakPlayer> doorTraverse){
		this.door = door;
		this.out = out;
		this.doorTraverse = doorTraverse;
		if(doorTraverse != null)
			Bukkit.getServer().getPluginManager().registerEvents(this, Jailbreak.getPlugin());
	}
	
	public void open(){
		for(Block b : door)
			b.setType(Material.AIR);
		open = true;
	}
	
	public void close(){
		for(Block b : door)
			b.setType(Material.IRON_FENCE);
		open = false;
	}
	
	public boolean isOpen(){
		return open;
	}
	
	public boolean isOnBarrier(Location loc){
		return door.contains(loc);
	}
	
	public boolean isOut(Location loc){
		return out.contains(loc);
	}

	public void trigger() {
		if (isOpen())
			close();
		else
			open();
	}
	
	@EventHandler
	public void onEscape(PlayerMoveEvent e){
		Player player = e.getPlayer();
		if (Jailbreak.isPlayer(player) && isOnBarrier(e.getFrom()) && isOut(e.getTo())){
			doorTraverse.accept(Jailbreak.getPlayer(player));
		}
	}
	
}
