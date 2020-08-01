package me.cxom.jailbreak3.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

import me.cxom.jailbreak3.Jailbreak;

public class CancelledEvents implements Listener{

	@EventHandler
	public void onProtectedDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player
				&& isProtectedDamageCause(e)
				&& Jailbreak.isPlayer((Player) e.getEntity())){
			e.setCancelled(true);
		}
	}
	
	private boolean isProtectedDamageCause(EntityDamageEvent e) {
		DamageCause cause = e.getCause();
		return cause == DamageCause.FALL
			|| cause == DamageCause.ENTITY_EXPLOSION;
	}
	
	@EventHandler
	public void onPlayerOpenInventoryEvent(InventoryOpenEvent e) {
		if (e.getInventory().getType() != InventoryType.PLAYER && Jailbreak.isPlayer(e.getPlayer().getUniqueId())) {			
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerBlockBreak(BlockBreakEvent e) {
		if (Jailbreak.isPlayer(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerBlockPlace(BlockPlaceEvent e) {
		if (Jailbreak.isPlayer(e.getPlayer())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		if (e.getEntity() instanceof Player && Jailbreak.isPlayer((Player) e.getEntity())){
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerRegainHealth(EntityRegainHealthEvent e) {
		if (e.getEntity() instanceof Player && Jailbreak.isPlayer((Player) e.getEntity())){
			e.setCancelled(true);
		}
	}
	
}
