package de.tobiaswegner.communication.cul4java.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

import de.tobiaswegner.communication.cul4java.CULInterface;
import de.tobiaswegner.communication.cul4java.CULListener;
import de.tobiaswegner.communication.cul4java.FHTListener;
import de.tobiaswegner.communication.cul4java.FS20Listener;

public class CULTransceiver implements CULInterface, Runnable {
	protected BufferedReader br = null; 
	protected InputStream is = null;
	protected BufferedWriter bw = null;
	protected OutputStream os = null;
	protected BufferedWriter logWriter = null;
	
	protected FS20Handler fs20Handler = new FS20Handler();
	protected FHTHandler fhtHandler = new FHTHandler();
	
	protected boolean active = true;
	Thread readThread = null;
	
	@Override
	public void Open(InputStream is, OutputStream os) {
		this.is = is;
		this.os = os;
		
		String logFileName = System.getProperty("fs20.log");
		
		if (logFileName != null)
		{
			try {
				logWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFileName, true)));
				
				logWriter.write(new Date().toString() +  " LOG STARTED\r\n");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		br = new BufferedReader(new InputStreamReader(is));
		bw = new BufferedWriter(new OutputStreamWriter(os));

		RAW_Send ("X43\n");

		readThread = new Thread(this);
		readThread.setName("[FS20] - Receiver Thread");
		readThread.start();
	}

	@Override
	public void Close() {
		active = false;

		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void registerListener(CULListener listener) {
		if (listener instanceof FS20Listener)
			fs20Handler.registerListener((FS20Listener)listener);

		if (listener instanceof FHTListener)
			fhtHandler.registerListener((FHTListener)listener);
	}

	@Override
	public void unregisterListener(CULListener listener) {
		if (listener instanceof FS20Listener)
			fs20Handler.unregisterListener((FS20Listener)listener);

		if (listener instanceof FHTListener)
			fhtHandler.unregisterListener((FHTListener)listener);
	}

	@Override
	public void FS20_Send(String HouseCode, String Address, String Command) {
		String sendString = fs20Handler.Send(HouseCode, Address, Command);
		
		if (sendString.length() > 0)
			RAW_Send (sendString);
	}

	@Override
	public void FHT_Send() {
		// TODO Auto-generated method stub

	}	
	
	public void RAW_Send (String sendString) {
		synchronized (bw) {
			try {
				bw.write(sendString);
				bw.flush();
				
				if (logWriter != null)
				{
					synchronized (logWriter) {
						logWriter.write(new Date().toString() +  " OUT:" + sendString + "\r\n");
						logWriter.flush();
					}
				}
				
				System.out.print("CUL sent: " + sendString);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}	

	public void run() {		
		while (active) {
			try {
				try {
					int c = -1;
					StringBuffer inbuf = new StringBuffer();

					while ((c = is.read()) >= 0) {
						if ((c != 10) && (c != 13))
							inbuf.append((char) c);
						else
							break;
					}

					String line = inbuf.toString();

					if (line.length() > 0)
					{
						if (logWriter != null) {
							synchronized (logWriter) {
								logWriter.write(new Date().toString() +  " IN:" + line + "\r\n");								
								logWriter.flush();
							}
						}
						
						System.out.println ("CUL received: " + line);
						
						if (line.startsWith("F"))
						{
							//is FS20 frame
							fs20Handler.ParseFS20 (line);
						}
						else if (line.startsWith("T")) {
							//is FHT frames
							fhtHandler.ParseFHT(line);
						} 
						else if (line.equals("LOVF")) {
							// no send time available anymore
							System.out.println("No fs20 send time available");
						}
						else {
							System.out.println("CUL received: " + line);
						}
					}
					else
						Thread.sleep(100);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
					active = false;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
	
}
