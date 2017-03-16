package eu.kingconquest.worldregain;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import eu.kingconquest.worldregain.datastorage.YmlStorage;
import eu.kingconquest.worldregain.util.Message;
import eu.kingconquest.worldregain.util.MessageType;

public class Main extends JavaPlugin implements Listener{
	private static Main instance;

	/**
	 * Plugin Startup
	 * @return void
	 */
	@Override
	public void onEnable(){
		instance = this;
		
		YmlStorage.load();
		new Message(null, MessageType.CONSOLE, "&6|==============={Prefix}==============|");
		new Message(null, MessageType.CONSOLE, "&6|&2 Version: " + getDescription().getVersion());
		new Message(null, MessageType.CONSOLE, "&6|&2 Hooks:");
		//Hooks.output();
		new Message(null, MessageType.CONSOLE, "&6|&2 Configs:");
		YmlStorage.output();
		new Message(null, MessageType.CONSOLE, "&6|=======================================|");

		//ServerRestartListener.onServerRestart(Bukkit.getOnlinePlayers());
		setListeners();
		
	}

	private void setListeners(){
		//this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
	}
	
	/**
	 * On Command /c /kc, kingc, conquest, kingconquest
	 * @return boolean
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		//this.getCommand("kingconquest").setExecutor(new HomeCommand());
		return true;
	}
	
	/**
	 * Get Instance of Plugin
	 * @return Plugin Instance
	 */
	public static final Main getInstance(){
		return instance;
	}
	/**
	 * Plugin Shutdown
	 * @return void
	 */
	@Override
	public void onDisable() {
		new Message(null, MessageType.CONSOLE, "&6|==============={Prefix}==============|");
		new Message(null, MessageType.CONSOLE, "&6|&2 Configs:");
		YmlStorage.remove();
		YmlStorage.save();
		new Message(null, MessageType.CONSOLE, "&6|=======================================|");
		
		YmlStorage.clear();
		
		getServer().getServicesManager().unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
	}
}
