package de.tobiaswegner.communication.cul4java;

import java.io.InputStream;
import java.io.OutputStream;

public interface CULInterface {
	public void Open (InputStream is, OutputStream os);
	public void Close ();
	public boolean isOpen ();
	
	public void registerListener (CULListener listener);
	public void unregisterListener (CULListener listener);
	
	public void FS20_Send (String HouseCode, String Address, String Command);
	public void FHT_Send ();
	
	public void Decode(String cmdLine);
}
