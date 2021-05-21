package me.cxom.jailbreak3.events.custom;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.cxom.jailbreak3.Jailbreak;
import me.cxom.jailbreak3.events.CancelledEvents;

public class JailbreakDeathEventCaller implements Listener {

	@EventHandler
	public void onJailbreakDeath(EntityDamageEvent e){
		if (! (Jailbreak.isPlayer(e.getEntity().getUniqueId()))) return;
		Player player = (Player) e.getEntity();
		if (CancelledEvents.isProtectedDamageCause(e)) return;
		if (e.getCause() == DamageCause.VOID || e.getFinalDamage() >= player.getHealth()) {
			Bukkit.getServer().getPluginManager().callEvent(new JailbreakDeathEvent(Jailbreak.getPlayer(player), e));
		}
	}

	//TODO JailbreakDeathByJailbreakPlayerEvent
	/*if (e instanceof EntityDamageByEntityEvent){
		EntityDamageByEntityEvent e2 = (EntityDamageByEntityEvent) e;
		if(e2.getDamager() instanceof Player && Jailbreak.isPlayer((Player) e2.getDamager())){
		
		}
	}*/
	
}
