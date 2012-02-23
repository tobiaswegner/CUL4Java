package de.tobiaswegner.communication.cul4java;

public interface FHTListener extends CULListener {
	public void FHT8bReceived (String device, int state);
	
	public void FHT80bReceivedTemperature (String device, double temperature);
}
