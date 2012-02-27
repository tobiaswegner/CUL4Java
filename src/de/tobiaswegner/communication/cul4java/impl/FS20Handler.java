package de.tobiaswegner.communication.cul4java.impl;

import java.util.ArrayList;
import java.util.Iterator;

import de.tobiaswegner.communication.cul4java.FS20Listener;

public class FS20Handler {
	ArrayList<FS20Listener> listeners = new ArrayList<FS20Listener>();
	
	public void registerListener (FS20Listener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}
	
	public void unregisterListener (FS20Listener listener) {
		if (listeners.contains(listener))
			listeners.remove(listener);
	}
	
	public String Send(String HouseCode, String Address, String Command) {
		if ((HouseCode.length() == 4) && (Address.length() == 2) && (Command.length() == 2)) {			
			String sendString = "F" + HouseCode + Address + Command + "\r\n";

			return sendString;
		}
		
		return "";
	}	
	
	protected void ParseFS20 (String line)
	{
		try {
			String houseCode = (line.substring(1, 5));
			String address = (line.substring(5, 7));
			String command = line.substring(7, 9);
	
			int houseCodeI = Integer.parseInt(houseCode, 16);
			int groupAddressI = Integer.parseInt(address, 16);
			byte commandI = Byte.parseByte(command, 16);
	
			Iterator<FS20Listener> listenerIterator = listeners.iterator();
	
			while (listenerIterator.hasNext())
			{
				FS20Listener listener = listenerIterator.next();
				listener.MessageReceived(houseCodeI, groupAddressI, commandI);
			}
		} catch (StringIndexOutOfBoundsException e) {								
			e.printStackTrace();
			
			System.out.println("line: " + line);								
		}		
	}
}
