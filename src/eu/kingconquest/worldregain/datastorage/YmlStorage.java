package eu.kingconquest.worldregain.datastorage;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import eu.kingconquest.worldregain.Main;
import eu.kingconquest.worldregain.util.Message;
import eu.kingconquest.worldregain.util.MessageType;
import eu.kingconquest.worldregain.util.Validate;

public class YmlStorage extends YamlConfiguration{
	private String name;
	private File file;
	private String defaults;

	//Instance Specific
	/**
	 * Creates new Config File, without defaults
	 * @param path - String
	 * @param fileName - String
	 */
	public YmlStorage(String path, String fileName) {
		this(path, fileName, null);
	}

	/**
	 * Creates new Config File, with defaults
	 * @param path - String
	 * @param fileName - String
	 * @param defaultsName - String
	 */
	public YmlStorage(String path, String fileName, String defaultsName) {
		defaults = defaultsName;
		String pathway = (path == null) 
				? Main.getInstance().getDataFolder() + File.separator + fileName 
						: Main.getInstance().getDataFolder() + File.separator + path + File.separator + fileName;

		try{
			file = new File(pathway);
			reload();
			setName(fileName);
			addConfig(this);
			loadMsg.put("&6| --&3 " +  fileName, true);	
		}catch(Exception e){
			e.printStackTrace();
			loadMsg.put("&6| --&3 " +  fileName, false);	
		}
	}

	/**
	 * Reload configuration
	 * return void
	 */
	public boolean reload() {
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}catch (IOException exception){
				Main.getInstance().getLogger().severe("Error while creating file " + file.getName());
				return false;
			}
		}

		try {
			load(file);
			if (defaults != null){
				InputStreamReader reader = new InputStreamReader(Main.getInstance().getResource(defaults));
				FileConfiguration defaultsConfig = YamlConfiguration.loadConfiguration(reader);       

				setDefaults(defaultsConfig);
				options().copyDefaults(true);

				reader.close();
				saveConfig();
				return true;
			}
		}catch (IOException e){
			Main.getInstance().getLogger().severe("Error while loading file " + file.getName());
			e.printStackTrace();
			return false;
		}
		catch (InvalidConfigurationException e){
			Main.getInstance().getLogger().severe("Error while loading file " + file.getName());
			e.printStackTrace();
			return false;
		}
		return false;
	}

	/**
	 * Save configuration
	 * @return void
	 */
	public void saveConfig() {
		try {
			options().indent(2);
			save(file);
		}catch (IOException exception){
			exception.printStackTrace();
			Main.getInstance().getLogger().severe("Error while saving file " + file.getName());
		}
	}

	/**
	 * Set Location to config
	 * @param config - ConfigManager instance
	 * @param pathway - String
	 * @param key - Location
	 * @return void
	 */
	public void setLocation(String pathway, Location key){
		if (Validate.notNull(pathway) && Validate.notNull(key.getWorld())){
			this.set(pathway + ".X", key.getX());
			this.set(pathway + ".Y", key.getY());
			this.set(pathway + ".Z", key.getZ());
			return;
		}
	}

	/**
	 * Get location from config
	 * @param pathway - String
	 * @return Location
	 */
	public Location getLocation(World world, String pathway){
		Double X = this.getDouble(pathway + ".X");
		Double Y = this.getDouble(pathway + ".Y");
		Double Z = this.getDouble(pathway + ".Z");
		Location loc = new Location(world, X, Y, Z);
		return loc;
	}

	//Config Specific
	//Statics
	private static String headerMsg = "&6| - &aSuccess:";
	private static String errorMsg = "&6| - &cFailed:";
	private static HashMap<String, Boolean> loadMsg = new HashMap<>();
	private static HashMap<String, Boolean> saveMsg = new HashMap<>();
	private static HashMap<String, Boolean> removeMsg = new HashMap<>();
	private static int saveTaskID = 0;

	public static void save(){
		if (saveTaskID > 0){
			saveTaskID = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), new Runnable(){
				@Override
				public void run(){
					//Save data
					worlds.forEach(uniqueID->{
						//World world = Bukkit.getWorld(uniqueID);
					});
					saveMsg.clear();
				}
			}, 10, Long.valueOf(getStr("AutoSaveInterval"))).getTaskId();
		}else{
			headerMsg = "&6| - &aSaved:";
			worlds.forEach(uniqueID->{
				//World world = Bukkit.getWorld(uniqueID);
				output();
			});
		}
	}
	public static void load(){
		registerFiles();
		loadLanguage();
		loadDefault();
		getWorlds().forEach(aWorld->{
			//World world = Bukkit.getWorld(aWorld);
			headerMsg = "&6| - &aLoaded:";
		});
	}
	public static void remove(){
		headerMsg = "&6| - &cRemoved:";
		worlds.forEach(uniqueID->{
			//World world = Bukkit.getWorld(uniqueID);
		});
		output();
	}

	public static Set<String> getPathSection(YmlStorage c, String path){
		Validate.notNull(c.getConfigurationSection(path).getKeys(false), "&cPath Section Failure: \n&3" + path);
		return c.getConfigurationSection(path).getKeys(false);
	}

	public static void output(){
		if (loadMsg.containsValue(true)) {
			new Message(null, MessageType.CONSOLE, headerMsg);
			loadMsg.forEach((s,b)->{
				if (b) {
					new Message(null, MessageType.CONSOLE, s);
				}
			});
		}
		if (loadMsg.containsValue(false)) {
			new Message(null, MessageType.CONSOLE, errorMsg);
			loadMsg.forEach((s,b)->{
				if (!b)
					new Message(null, MessageType.CONSOLE, s);
			});
		}
		loadMsg.clear();

		if (saveMsg.containsValue(true)) {
			new Message(null, MessageType.CONSOLE, headerMsg);
			saveMsg.forEach((s,b)->{
				if (b) {
					new Message(null, MessageType.CONSOLE, s);
				}
			});
		}
		if (saveMsg.containsValue(false)) {
			new Message(null, MessageType.CONSOLE, errorMsg);
			saveMsg.forEach((s,b)->{
				if (!b)
					new Message(null, MessageType.CONSOLE, s);
			});
		}
		saveMsg.clear();

		if (removeMsg.containsValue(true)) {
			new Message(null, MessageType.CONSOLE, headerMsg);
			removeMsg.forEach((s,b)->{
				if (b) {
					new Message(null, MessageType.CONSOLE, s);
				}
			});
		}
		if (removeMsg.containsValue(false)) {
			new Message(null, MessageType.CONSOLE, errorMsg);
			removeMsg.forEach((s,b)->{
				if (!b)
					new Message(null, MessageType.CONSOLE, s);
			});
		}
		removeMsg.clear();
	}


	//LOAD
	//Config Loads
	public static boolean loadDefault(){
		//YmlStorage config = getConfig("Config");
		return true;
	}
	public static boolean loadLanguage(){
		//YmlStorage lang = getConfig("Language");
		return true;
	}
	//Data Files
	
	
	private static ArrayList<UUID> worlds = new ArrayList<UUID>();
	public static ArrayList<UUID> getWorlds(){
		return worlds;
	}
	public static void addWorld(World world) {
		worlds.add(world.getUID());
	}
	public static World getWorld(UUID uuid) {
		for (UUID uniqueID : worlds){
			if (Bukkit.getWorld(uniqueID).getUID().equals(uuid))
				return Bukkit.getWorld(uniqueID);
		}
		return null;
	}
	public static boolean isActiveWorld(String name) {
		for (UUID uniqueID : worlds){
			Validate.notNull(Bukkit.getWorld(uniqueID), "Not a known world UUID" + uniqueID);
			if (Bukkit.getWorld(uniqueID).getName().equals(name))
				return true;
		}
		return false;
	}

	//UUID of World
	public static HashMap<String, String> strings = new HashMap<String, String>();
	private static HashMap<UUID, HashMap<String, Boolean>> booleans = new HashMap<>();
	private static HashMap<UUID, HashMap<String, Integer>> integers = new HashMap<>();
	private static HashMap<UUID, HashMap<String, Double>> doubles = new HashMap<>();
	private static HashMap<UUID, HashMap<String, Long>> longs = new HashMap<>();
	public static String getStr(String str) {
		return strings.get(str);
	}
	public static boolean getBoolean(String str, Location loc) {
		HashMap<String, Boolean> map2 = booleans.get(loc.getWorld().getUID()) ;
		return map2.get(str);
	}
	public static int getInteger(String str, Location loc) {
		HashMap<String, Integer> map2 = integers.get(loc.getWorld().getUID());
		return map2.get(str);
	}
	public static Double getDouble(String str, Location loc) {
		HashMap<String, Double> map2 = doubles.get(loc.getWorld().getUID()) ;
		return map2.get(str);
	}
	public static Long getLong(String str, Location loc) {
		HashMap<String, Long> map2 = longs.get(loc.getWorld().getUID());
		return map2.get(str);
	}

	private static ArrayList<YmlStorage> configs = new ArrayList<>();
	public static boolean hasConfigs(){
		if (configs.size() != 0)
			return true;
		return false;
	}
	public static ArrayList<YmlStorage> getConfigs(){
		return configs;
	}
	public static YmlStorage getConfig(String name){
		for (YmlStorage c : getConfigs()){
			if (c.getName().replace(".yml", "").equals(name)){
				c.reload();
				return c;
			}
		}
		new Message(null, MessageType.CONSOLE, "&4ERROR: Could not find config: " + name + " file");
		new Message(null, MessageType.CONSOLE, "&4: Wrong name?");
		return null;
	}
	public static void addConfig(YmlStorage config){
		configs.add(config);
	}

	public static void clear() {
		configs.clear();
	}

	@Override
	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public static void registerFiles(){
		new YmlStorage(null, "Config.yml", "Config.yml");
		new YmlStorage(null, "Language.yml", "Language.yml");

		try(Stream<Path> paths = Files.walk(Paths.get(Main.getInstance().getDataFolder() + File.separator + "Data"))) {
			paths.forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					new YmlStorage("Data" + File.separator + "Users", filePath.getFileName().toString());
					loadMsg.put("&6| --&3 " +  filePath.getFileName().toString(), true);		
				}
			});
		}catch (IOException e){}
	}
}