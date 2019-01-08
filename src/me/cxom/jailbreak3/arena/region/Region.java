package me.cxom.jailbreak3.arena.region;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Region implements Area{
	
	Location min;
	Location max;
	Region parent;
	
	public Region(Location a, Location b) throws IllegalArgumentException{
		if (a.getWorld() != b.getWorld()){
			throw new IllegalArgumentException("Defining points cannot be in two different worlds!");
		}
		min = new Location(a.getWorld(), Math.min(a.getBlockX(), b.getBlockX()), Math.min(a.getBlockY(), b.getBlockY()), Math.min(a.getBlockZ(), b.getBlockZ()));
		max = new Location(a.getWorld(), Math.max(a.getBlockX(), b.getBlockX()), Math.max(a.getBlockY(), b.getBlockY()), Math.max(a.getBlockZ(), b.getBlockZ()));
	}
	
	@Override
	public boolean contains(Location l){
		l=l.getBlock().getLocation();
		return isBetween(l, min, max);
	}
	
	public Location getMin(){
		return min;
	}
	
	public Location getMax(){
		return max;
	}
	
	public Region getParent(){
		return parent;
	}
	
	public World getWorld(){
		return min.getWorld();
	}
	
	public Location[] getCorners(){
		Location[] corners = new Location[8];
		int xMax = max.getBlockX();
		int xMin = min.getBlockX();
		int yMax = max.getBlockY();
		int yMin = min.getBlockY();
		int zMax = max.getBlockZ();
		int zMin = min.getBlockZ();
		World w = max.getWorld();
		corners[0] = new Location(w, xMax, yMax, zMax);
		corners[1] = new Location(w, xMax, yMax, zMin);
		corners[2] = new Location(w, xMax, yMin, zMax);
		corners[3] = new Location(w, xMax, yMin, zMin);
		corners[4] = new Location(w, xMin, yMax, zMax);
		corners[5] = new Location(w, xMin, yMax, zMin);
		corners[6] = new Location(w, xMin, yMin, zMax);
		corners[7] = new Location(w, xMin, yMin, zMin);
		return corners;
	}
	
	private static boolean isBetween(Location test, Location min, Location max){
		//if TEST is between MIN and MAX, returns TRUE; else returns FALSE
		if ( ! (test.getX() >= min.getX() && test.getX() <= max.getX())) return false;
		else if ( ! (test.getY() >= min.getY() && test.getY() <= max.getY())) return false;
		else if ( ! (test.getZ() >= min.getZ() && test.getZ() <= max.getZ())) return false;
		return true;
	}
	
	public boolean isInside(Region r){	
		if (this.getMax().getX() > r.getMax().getX()) return false;
		else if (this.getMax().getY() > r.getMax().getY()) return false;
		else if (this.getMax().getZ() > r.getMax().getZ()) return false;
		else if (this.getMin().getX() < r.getMin().getX()) return false;
		else if (this.getMin().getY() < r.getMin().getY()) return false;
		else if (this.getMin().getZ() < r.getMin().getZ()) return false;
		return true;
	}
	
	public boolean isPlanar(){
		return this.isHorizontallyPlanar() || this.isVerticallyPlanar();
	}
	
	public boolean isHorizontallyPlanar(){
		return (min.getY() == max.getY());
	}
	
	public boolean isVerticallyPlanar(){
		return (min.getX() == max.getX() || min.getZ() == max.getZ());
		
	}
	
//	@SuppressWarnings("deprecation")
//	public void set(Material m, byte data){
//		World world = this.getMin().getWorld();
//		for (int x = this.getMin().getBlockX(); x <= this.getMax().getBlockX(); x++){
//			for (int y = this.getMin().getBlockY(); y <= this.getMax().getBlockY(); y++){
//				for (int z = this.getMin().getBlockZ(); z <= this.getMax().getBlockZ(); z++){
//					Block b = new Location(world, x, y, z).getBlock();
//					b.setType(m);
//					b.setData(data);
//				}
//			}
//		}
//	}
//	
//	@SuppressWarnings("deprecation")
//	private void showBlocks(Plugin p, Location a, Location b, Location c, Location d, Material m, byte data){
//		final Block ab = a.getBlock();
//		final Block bb = b.getBlock();
//		final Block cb = c.getBlock();
//		final Block db = d.getBlock();
//		final Material am = ab.getType();
//		final Material bm = bb.getType();
//		final Material cm = cb.getType();
//		final Material dm = db.getType();
//		final byte ad = ab.getData();
//		final byte bd = bb.getData();
//		final byte cd = cb.getData();
//		final byte dd = db.getData();
//		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(p, new Runnable(){
//			public void run(){
//				ab.setType(am);
//				ab.setData(ad);
//				bb.setType(bm);
//				bb.setData(bd);
//				cb.setType(cm);
//				cb.setData(cd);
//				db.setType(dm);
//				db.setData(dd);
//			}
//		}, 100);
//		ab.setType(m);
//		ab.setData(data);
//		bb.setType(m);
//		bb.setData(data);
//		cb.setType(m);
//		cb.setData(data);
//		db.setType(m);
//		db.setData(data);
//	}
	
	public void setParent(Region r) throws IllegalArgumentException{
		if(!this.isInside(r)){
			throw new IllegalArgumentException("The child region is not inside the parent!");
		}
		parent = r;
	}
	
//	public void show(Plugin p, Material m, byte data){			//TO BE FINISHED, NEEDS ARRAYS OF STORED BLOCKS (OR, RATHER, ANOTHER METHOD FOR SUCH, INDIVIDUALLY, EACH TIME)
//		World world = this.getMin().getWorld();
//		Location[] corners = this.getCorners();
//		for (int x = this.getMin().getBlockX(); x <= this.getMax().getBlockX(); x++){
//			Location a = new Location(world, x, corners[4].getY(), corners[4].getZ());
//			Location b = new Location(world, x, corners[5].getY(), corners[5].getZ());
//			Location c = new Location(world, x, corners[6].getY(), corners[6].getZ());
//			Location d = new Location(world, x, corners[7].getY(), corners[7].getZ());
//			
//			showBlocks(p, a, b, c, d, m, data);
//		}
//		for (int y = this.getMin().getBlockY(); y <= this.getMax().getBlockY(); y++){
//			Location a = new Location(world, corners[2].getX(), y, corners[2].getZ());
//			Location b = new Location(world, corners[3].getX(), y, corners[3].getZ());
//			Location c = new Location(world, corners[6].getX(), y, corners[6].getZ());
//			Location d = new Location(world, corners[7].getX(), y, corners[7].getZ());
//
//			showBlocks(p, a, b, c, d, m, data);
//		}
//		for (int z = this.getMin().getBlockZ(); z <= this.getMax().getBlockZ(); z++){
//			Location a = new Location(world, corners[1].getX(), corners[1].getY(), z);
//			Location b = new Location(world, corners[3].getX(), corners[3].getY(), z);
//			Location c = new Location(world, corners[5].getX(), corners[5].getY(), z);
//			Location d = new Location(world, corners[7].getX(), corners[7].getY(), z);
//
//			showBlocks(p, a, b, c, d, m, data);
//		}
//		
//	}
	
	public boolean touches(Region r){
		Location[] corners = r.getCorners();
		for (Location corner : corners) if (isBetween(corner, min, max) == true) return true;
		return isBetween(min, r.getMin(), r.getMax()) && isBetween(max, r.getMin(), r.getMax());
	}
	
	public void write(FileConfiguration fc, String area, File file){
		fc.set(area + ".min.x", min.getX());
		fc.set(area + ".min.y", min.getY());
		fc.set(area + ".min.z", min.getZ());
		fc.set(area + ".max.x", max.getX());
		fc.set(area + ".max.y", max.getY());
		fc.set(area + ".max.z", max.getZ());
		fc.set(area + ".world", this.getWorld().getName());
		try{
			fc.save(file);
		}catch (IOException e){
			System.out.println("Could not save file!");
		}
	}

	@Override
	public Iterator<Block> iterator() {
		return new Iterator<Block>(){
			int i = min.getBlockX(), j = min.getBlockY(), k = min.getBlockZ();
			@Override
			public boolean hasNext() {
				return i <= max.getBlockX() && j <= max.getBlockY() && k <= max.getBlockZ();
			}

			@Override
			public Block next() {
				Location l = (new Location(getWorld(), i, j, k));
				k++;
				if (k > max.getBlockZ()){
					k = min.getBlockZ();
					j++;
					if (j > max.getBlockY()){
						j = min.getBlockY();
						i++;
					}
				}
				return min.getWorld().getBlockAt(l);
			}
			
		};
	}
	
}
