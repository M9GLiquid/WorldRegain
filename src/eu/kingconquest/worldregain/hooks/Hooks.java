package eu.kingconquest.worldregain.hooks;

import java.util.HashMap;

import eu.kingconquest.worldregain.util.Message;
import eu.kingconquest.worldregain.util.MessageType;
import eu.kingconquest.worldregain.util.Validate;


public class Hooks{
	private static String headerMsg = 	"&6| - &aHooked:";
	private static String errorMsg = 	"&6| - &cFailed:";
	private static HashMap<String, Boolean> msg = new HashMap<String, Boolean>();
	
	public static void output() {
		if (Validate.isNull(Vault.econ)){
		}
		
			if (msg.containsValue(true)) {
				new Message(null, MessageType.CONSOLE, headerMsg);
				msg.forEach((s,b)->{
					if (b) {
						new Message(null, MessageType.CONSOLE, s);
					}
				});
			}
			if (msg.containsValue(false)) {
				new Message(null, MessageType.CONSOLE, errorMsg);
				msg.forEach((s,b)->{
					if (!b)
					new Message(null, MessageType.CONSOLE, s);
				});
			}
			msg.clear();
	}
	public static void put(String str, Boolean b) {
		msg.put(str, b);
	}
}
