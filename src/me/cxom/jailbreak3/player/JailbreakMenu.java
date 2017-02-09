package me.cxom.jailbreak3.player;

import java.util.Arrays;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.cxom.jailbreak3.Jailbreak;
import me.cxom.jailbreak3.game.GameInstance;

public class JailbreakMenu implements Listener {

	private static final String title = "Jailbreak Games"; //TODO i18n (Comparing enum values, most likely, for events below)
	
	public static Inventory getMenu(){
		Map<String, GameInstance> games = Jailbreak.getGameMap();
		Inventory menu = Bukkit.createInventory(null, (games.size() / 9 + 1) * 9, title); 
		for (Map.Entry<String, GameInstance> gameEntry : games.entrySet()){
			GameInstance game = gameEntry.getValue();
			ItemStack gameMarker = game.getGameState().getMenuItem();
			ItemMeta meta = gameMarker.getItemMeta();
			meta.setDisplayName(ChatColor.BLUE + gameEntry.getKey());
			meta.setLore(Arrays.asList(game.getGameState().getChatColor() + game.getGameState().name(),
									   game.getLobby().getWaitingPlayers().size() + " player(s) in lobby.")); //TODO i18n
			gameMarker.setItemMeta(meta);
			menu.addItem(gameMarker);
		}
		return menu;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e){
		if (e.getCurrentItem() == null) return;
		if (!e.getCurrentItem().hasItemMeta()) return;
		if (title.equals(e.getClickedInventory().getName())){
			ItemStack clicked = e.getCurrentItem();
			if(! ChatColor.stripColor(clicked.getItemMeta().getLore().get(1)).equals("STOPPED")){
				Jailbreak.getGame(ChatColor.stripColor(clicked.getItemMeta().getDisplayName())).getLobby().addPlayer((Player) e.getWhoClicked());
				e.getWhoClicked().closeInventory();
			}
		}
	}
	
	@EventHandler
	public void onMenuDrag(InventoryDragEvent e){
		if(e.getInventory().getName().equals(title)){
			e.setCancelled(true);
		}
	}
	
}
