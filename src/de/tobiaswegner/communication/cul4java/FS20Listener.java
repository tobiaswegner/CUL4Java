package de.tobiaswegner.communication.cul4java;

public interface FS20Listener extends CULListener {
	public void MessageReceived (int Housecode, int address, byte command);
}
