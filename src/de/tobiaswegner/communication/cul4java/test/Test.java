package de.tobiaswegner.communication.cul4java.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;

import de.tobiaswegner.communication.cul4java.CULInterface;
import de.tobiaswegner.communication.cul4java.CULListener;
import de.tobiaswegner.communication.cul4java.impl.CULTransceiver;

public class Test {
	protected static CommPort commPort = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		InputStream is = null;
		OutputStream os = null;

		try {
			String portName = System.getProperty("cul.port");
			
			if (portName == null)
				portName = "COM8";
			
			if (!portName.equalsIgnoreCase("null")) {
				System.out.println("CUL4Java using port " + portName + "\r\n");
	
				SerialPort serialPort = null;
	
				if (portName.startsWith("/dev/")) {
					File portFile = new File(portName);
					is = new FileInputStream(portFile);
					os = new FileOutputStream(portFile);
				} else {
					try {
					CommPortIdentifier portIdentifier = CommPortIdentifier
							.getPortIdentifier(portName);
	
					commPort = portIdentifier.open("CUL4Java", 2000);
	
					if (commPort instanceof SerialPort) {
						serialPort = (SerialPort) commPort;
						serialPort.setInputBufferSize(10240);
						serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
								SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);
	
						is = serialPort.getInputStream();
						os = serialPort.getOutputStream();
					}}
					catch (NoClassDefFoundError ncdfe) {
						
					}
					catch (NoSuchPortException nspe) {
						System.out.print("CUL4Java: Error, could not open port " + portName);
					}
				}
			} 
			else 
			{
				System.out.println ("CUL4Java not using external port\r\n");
				
	//			transceiver = new FS20TransceiverNull();
				
	//			((FS20AbstractTransceiver)transceiver).ParseFHT("T10139502");
	//			((FS20AbstractTransceiver)transceiver).ParseFHT("T194500AA00");
				
				is = new ByteArrayInputStream("T194500AA00".getBytes());
				os = new ByteArrayOutputStream();
			}
		} catch (Exception e) {
		}

		CULInterface culInterface = new CULTransceiver();

		culInterface.Open(is, os);
		
//		culInterface.FS20_Send(HouseCode, Address, Command);
		
		culInterface.FS20_Send("010A", "01", "11");
		
		culInterface.Close();
	}

}
