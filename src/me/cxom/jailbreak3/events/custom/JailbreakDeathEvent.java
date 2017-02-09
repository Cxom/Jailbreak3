package me.cxom.jailbreak3.events.custom;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

import me.cxom.jailbreak3.player.JailbreakPlayer;

public class JailbreakDeathEvent extends Event {
	////
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
	////
	
	private final JailbreakPlayer jp;
	private final EntityDamageEvent ede;
	
	public JailbreakDeathEvent(JailbreakPlayer jp, EntityDamageEvent ede) {
			this.jp = jp;
			this.ede = ede;
	}

	public JailbreakPlayer getJailbreakPlayer() {
		return jp;
	}
	
	public EntityDamageEvent getEntityDamageEvent(){
		return ede;
	}
	
}
