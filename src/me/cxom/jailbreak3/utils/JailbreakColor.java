package me.cxom.jailbreak3.utils;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;

@SuppressWarnings("serial")
public class JailbreakColor extends Color {

	// Bukkit Colors:

		public static final JailbreakColor WHITE = new JailbreakColor(255, 255, 255, ChatColor.WHITE);
		public static final JailbreakColor YELLOW = new JailbreakColor(255, 255, 85, ChatColor.YELLOW);
		public static final JailbreakColor LIGHT_PURPLE = new JailbreakColor(255, 85, 255, ChatColor.LIGHT_PURPLE);
		public static final JailbreakColor RED = new JailbreakColor(255, 85, 85, ChatColor.RED);
		public static final JailbreakColor AQUA = new JailbreakColor(85, 255, 255, ChatColor.AQUA);
		public static final JailbreakColor GREEN = new JailbreakColor(85, 255, 85, ChatColor.GREEN);
		public static final JailbreakColor BLUE = new JailbreakColor(85, 85, 255, ChatColor.BLUE);
		public static final JailbreakColor DARK_GRAY = new JailbreakColor(85, 85, 85, ChatColor.DARK_GRAY);
		public static final JailbreakColor GRAY = new JailbreakColor(170, 170, 170, ChatColor.GRAY);
		public static final JailbreakColor GOLD = new JailbreakColor(255, 170, 0, ChatColor.GOLD);
		public static final JailbreakColor DARK_PURPLE = new JailbreakColor(170, 0, 170, ChatColor.DARK_PURPLE);
		public static final JailbreakColor DARK_RED = new JailbreakColor(170, 0, 0, ChatColor.DARK_RED);
		public static final JailbreakColor DARK_AQUA = new JailbreakColor(0, 170, 170, ChatColor.DARK_AQUA);
		public static final JailbreakColor DARK_GREEN = new JailbreakColor(0, 170, 0, ChatColor.DARK_GREEN);
		public static final JailbreakColor DARK_BLUE = new JailbreakColor(0, 0, 170, ChatColor.DARK_BLUE);
		public static final JailbreakColor BLACK = new JailbreakColor(0, 0, 0, ChatColor.BLACK);

		private static final Map<String, JailbreakColor> defaults;
		static{
			defaults = new HashMap<String, JailbreakColor>();
			defaults.put("WHITE", JailbreakColor.WHITE);
			defaults.put("YELLOW", JailbreakColor.YELLOW);
			defaults.put("LIGHT_PURPLE", JailbreakColor.LIGHT_PURPLE);
			defaults.put("RED", JailbreakColor.RED);
			defaults.put("AQUA", JailbreakColor.AQUA);
			defaults.put("GREEN", JailbreakColor.GREEN);
			defaults.put("BLUE", JailbreakColor.BLUE);
			defaults.put("DARK_GRAY", JailbreakColor.DARK_GRAY);
			defaults.put("GRAY", JailbreakColor.GRAY);
			defaults.put("GOLD", JailbreakColor.GOLD);
			defaults.put("DARK_PURPLE", JailbreakColor.DARK_PURPLE);
			defaults.put("DARK_RED", JailbreakColor.DARK_RED);
			defaults.put("DARK_AQUA", JailbreakColor.DARK_AQUA);
			defaults.put("DARK_GREEN", JailbreakColor.DARK_GREEN);
			defaults.put("DARK_BLUE", JailbreakColor.DARK_BLUE);
			defaults.put("BLACK", JailbreakColor.BLACK);
		}
		
		/* 
		 * Wool & Clay Colors
		 * RED(), ORANGE(), YELLOW(), GREEN(), BLUE(), PURPLE(), 
		 * LIME(), MAGENTA(), LIGHTBLUE(),
		 * PINK(), CYAN(), BROWN(),
		 * WHITE(), LIGHTGRAY(), GRAY(), BLACK();
		 */
		
		public static Collection<JailbreakColor> getDefaults(){
			return defaults.values();
		}
		
		public static ChatColor getNearestChatColor(int red, int green, int blue) {
			double distance = 500;
			ChatColor closest = ChatColor.WHITE;
			for (JailbreakColor c : defaults.values()) {
				double newDistance = 
						Math.sqrt(Math.pow((double) (red - c.getRed()), 2)
								+ Math.pow((double) (green - c.getGreen()), 2)
								+ Math.pow((double) (blue - c.getBlue()), 2));
				if (newDistance < distance) {
					distance = newDistance;
					closest = c.getChatColor();
				}
			}
			return closest;
		}
		
		public static JailbreakColor valueOf(String colorName){
			for(String color : defaults.keySet()){
				if(colorName.equalsIgnoreCase(color)
				|| colorName.equalsIgnoreCase(color.replaceAll("_", ""))){
					return defaults.get(color);
				}
			}
			System.out.println("No color found: " + colorName);
			return JailbreakColor.WHITE;
		}
	
		//------------------------------------------------------------------//

		private ChatColor chatColor;
		
		public JailbreakColor(int red, int green, int blue){
			this(red, green, blue, getNearestChatColor(red, green, blue));
		}
		
		public JailbreakColor(int red, int green, int blue, ChatColor chatColor){
			super(red, green, blue);
			this.chatColor = chatColor;
		}
		
		public ChatColor getChatColor(){
			return chatColor;
		}
		
		public void setChatColor(ChatColor chatColor){
			this.chatColor = chatColor;
		}
		
		public org.bukkit.Color getBukkitColor(){
			return org.bukkit.Color.fromRGB(this.getRed(), this.getGreen(), this.getBlue());
		}
	
}

