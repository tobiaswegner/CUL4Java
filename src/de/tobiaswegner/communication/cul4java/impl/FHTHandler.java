package de.tobiaswegner.communication.cul4java.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import de.tobiaswegner.communication.cul4java.FHTListener;

public class FHTHandler {
	HashMap<String, Integer> valueCache = new HashMap<>();
	
	ArrayList<FHTListener> listeners = new ArrayList<>();
	
	public void registerListener (FHTListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}
	
	public void unregisterListener (FHTListener listener) {
		if (listeners.contains(listener))
			listeners.remove(listener);
	}

	protected void ParseFHT (String line)
	{
		try
		{
			System.out.println("Received FHT frame: " + line);
			
			if (line.length() >= 13)
			{
				//is FHT8v frame
				String device = line.substring(1, 5); // dev
				String command = line.substring(5, 7); // cde
				int cde = Integer.parseInt(command, 16);
				String origin = line.substring(7, 9); // ??
				String argument = line.substring(9, 11); // val
				
				String cmd = FHTConstants.getIDByCode(cde);
				
				if (!cmd.equals("unknown")) {
					switch (cde) {
					case FHTConstants.FHT_DESIRED_TEMP:
						double desiredTemperature = ((double)Integer.parseInt(argument, 16)) / 2.0;
						
						for (FHTListener fhtListener : listeners) {
							fhtListener.FHT80bReceivedValue(device, "desiredTemperature", desiredTemperature);
						}

						System.out.println("FHT " + device + ": desired temp = " + desiredTemperature);

						break;
					case FHTConstants.FHT_MEASURED_TEMP_LOW:
						valueCache.put(device + "lowtemp", new Integer(Integer.parseInt(argument, 16)));
						
						break;
					case FHTConstants.FHT_MEASURED_TEMP_HIGH:
						Integer lowtemp = valueCache.get(device + "lowtemp");
						
						if (lowtemp != null) {
							double temperature = (double)lowtemp + ((double)Integer.parseInt(argument, 16)) * 256.0;
							
							temperature /= 10.0;
							
							for (FHTListener fhtListener : listeners) {
								fhtListener.FHT80bReceivedValue(device, "currentTemperature", temperature);
							}
						
							System.out.println("FHT " + device + ": measured temp = " + temperature);
						}

						break;
					case FHTConstants.FHT_ACTUATOR_0:
					case FHTConstants.FHT_ACTUATOR_1:
					case FHTConstants.FHT_ACTUATOR_2:
					case FHTConstants.FHT_ACTUATOR_3:
					case FHTConstants.FHT_ACTUATOR_4:
					case FHTConstants.FHT_ACTUATOR_5:
					case FHTConstants.FHT_ACTUATOR_6:
					case FHTConstants.FHT_ACTUATOR_7:
					case FHTConstants.FHT_ACTUATOR_8:
						double valve = ((double)Integer.parseInt(argument, 16)) / 255.0;
						
						for (FHTListener fhtListener : listeners) {
							fhtListener.FHT80bReceivedValue(device, FHTConstants.getIDByCode(cde), valve);
						}

						break;
					default:
						System.out.print("FHT " + device + ": " + cmd + "=" + argument + "\r\n");
					}
				}
			}
			else if (line.length() == 11)
			{
				//is FHT80b frame
				String device = line.substring(1, 7);
				String argument = line.substring(7, 9);
				
				int state = 0;
				
				if ((argument.startsWith("1")) || (argument.startsWith("9")))
				{
					state = FHTConstants.FHT8b_STATE_LOWBAT;
					
					System.out.println("Sensor: " + device + " battery low");
				}
				
				if (argument.substring(1).equals("1"))
				{
					state = FHTConstants.FHT8b_STATE_WINDOW_OPEN;

					System.out.println("Sensor: " + device + " opened");
				}

				if (argument.substring(1).equals("2"))
				{
					state = FHTConstants.FHT8b_STATE_WINDOW_CLOSED;

					System.out.println("Sensor: " + device + " closed");
				}
				
				if (state != 0) {
					Iterator<FHTListener> listenerIterator = listeners.iterator();
					
					while (listenerIterator.hasNext())
					{
						FHTListener listener = listenerIterator.next();
						listener.FHT8bReceived(device, state);
					}
				}
			}
		} catch (StringIndexOutOfBoundsException e) {								
			e.printStackTrace();
			
			System.out.println("line: " + line);								
		}	
	}
	
	public String SetDesiredTemperature (String device, double temperature) {
		if ((temperature >= 5.5) && (temperature <= 30.5)) {
			int temp = (int) (temperature * 2.0);
			
			String command = "T" +  device + "41" + Integer.toHexString(temp) + "\r\n";
			
			return command.toUpperCase();
		}
		
		return "";
	}
}
