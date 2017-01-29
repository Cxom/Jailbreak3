package me.cxom.jailbreak3.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import me.cxom.jailbreak3.Jailbreak;

public class CancelledEvents implements Listener{

	@EventHandler
	public void onFallDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player
				&& e.getCause() == DamageCause.FALL
				&& Jailbreak.isPlayer((Player) e.getEntity())){
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
