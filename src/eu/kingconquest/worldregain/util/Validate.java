package eu.kingconquest.worldregain.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import eu.kingconquest.worldregain.Main;
import eu.kingconquest.worldregain.datastorage.YmlStorage;
import eu.kingconquest.worldregain.hooks.Vault;


public class Validate{
	/**
	 * Check to see whether the player is within an outpost
	 * @param player
	 * @param loc
	 * @return
	 */
	public static boolean isWithinArea(Location location1, Location location2, double radius, double maxY, double minY){
		if (!location1.getWorld().equals(location2.getWorld()))
			return false;
		double dx = Math.abs(location1.getX() - location2.getX());
		double dz = Math.abs(location1.getZ() - location2.getZ());
		if (dx <= radius 
				&& dz <= radius 
				&& location1.getY() <= location2.getY() + maxY
				&& location1.getY() >= location2.getY() - minY){
			return true;
		}
		return false;
	}

	/**
	 * Check to see whether the player is within an outpost
	 * @param player
	 * @param loc
	 * @return
	 */
	public static boolean isWithinCaptureArea(Location player, Location target){
		double dx = Math.abs(player.getX() - target.getX());
		double dz = Math.abs(player.getZ() - target.getZ());
		double radius = YmlStorage.getDouble("CaptureDistance", player);
		double maxY = YmlStorage.getDouble("CaptureMaxY", player);
		double MinY = YmlStorage.getDouble("CaptureMinY", player);
		if (dx <= radius 
				&& dz <= radius 
				&& player.getY() <= target.getY() + maxY
				&& player.getY() >= target.getY() - MinY){
			return true;
		}
		return false;
	}

	public static boolean hasPerm(Player p, String path){
		if (Vault.perms.has(p, Main.getInstance().getName() + path))
			return true;
		return false; 
	}

	public static boolean notNull(Object reference){
		if (reference == null)
			return false;
		return true;
	}

	public static void notNull(Object reference, String errorMsg){
		if (reference == null)
			new Message(null, MessageType.ERROR, errorMsg);
	}

	public static boolean isNull(Object reference){
		if (reference != null)
			return false;
		return true;
	}

	public static void isNull(Object reference, String errorMsg){
		if (reference != null)
			new Message(null, MessageType.ERROR, errorMsg);
	}
}
