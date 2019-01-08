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
import me.cxom.jailbreak3.game.JailbreakGame;

public class JailbreakMenu implements Listener {

	private static final String title = "Jailbreak Games"; //TODO i18n (Comparing enum values, most likely, for events below)
	
	public static Inventory getMenu(){
		Map<String, JailbreakGame> games = Jailbreak.getGameMap();
		Inventory menu = Bukkit.createInventory(null, (games.size() / 9 + 1) * 9, title); 
		for (Map.Entry<String, JailbreakGame> gameEntry : games.entrySet()){
			JailbreakGame game = gameEntry.getValue();
			ItemStack gameMarker = game.getGameState().getMenuItem();
			ItemMeta meta = gameMarker.getItemMeta();
			meta.setDisplayName(ChatColor.BLUE + gameEntry.getKey());
			meta.setLore(Arrays.asList(game.getGameState().getChatColor() + game.getGameState().name(),
									   game.getLobby().getPlayers().size() + " player(s) in lobby.")); //TODO i18n
			gameMarker.setItemMeta(meta);
			menu.addItem(gameMarker);
		}
		return menu;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e){
		if (e.getClickedInventory() == null) return;
		if (title.equals(e.getClickedInventory().getName())){
			if (e.getCurrentItem() != null
			 && e.getCurrentItem().hasItemMeta()
			 && e.getCurrentItem().getItemMeta().hasLore()){
				ItemStack clicked = e.getCurrentItem();
				if(! ChatColor.stripColor(clicked.getItemMeta().getLore().get(1)).equals("STOPPED")){
					JailbreakGame game = Jailbreak.getGame(ChatColor.stripColor(clicked.getItemMeta().getDisplayName()));
					if (game != null){
						game.getLobby().addPlayerIfPossible((Player) e.getWhoClicked());
						e.getWhoClicked().closeInventory();
					}
				}
			}
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onMenuDrag(InventoryDragEvent e){
		if(e.getInventory().getName().equals(title)){
			e.setCancelled(true);
		}
	}
	
}
