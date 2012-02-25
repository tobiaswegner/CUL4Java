package de.tobiaswegner.communication.cul4java;

public interface FHTListener extends CULListener {
	public void FHT8bReceived (String device, int state);
	
	public void FHT80bReceivedValue (String device, String type, double temperature);
}
