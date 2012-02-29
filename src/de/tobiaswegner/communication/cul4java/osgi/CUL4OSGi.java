package de.tobiaswegner.communication.cul4java.osgi;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;

import de.tobiaswegner.communication.cul4java.CULInterface;
import de.tobiaswegner.communication.cul4java.impl.CULTransceiver;

public class CUL4OSGi {
	protected ComponentContext context = null;
	protected CULInterface culInterface = new CULTransceiver();
	protected ServiceRegistration<CULInterface> culServiceRegistration = null;

	protected Object commPort = null;
	
	protected void activate(ComponentContext context) {
		this.context = context;
	
		try {
			String portName = System.getProperty("cul.port");
			
			if (portName == null)
				portName = "COM8";
			
			if (!portName.equalsIgnoreCase("null")) {
				System.out.println("CUL4OSGi using port " + portName + "\r\n");
	
				InputStream in = null;
				OutputStream out = null;
	
				if (portName.startsWith("/dev/")) {
					File portFile = new File(portName);
					in = new FileInputStream(portFile);
					out = new FileOutputStream(portFile);
				} else {
					try {
						CommPortIdentifier portIdentifier = CommPortIdentifier
								.getPortIdentifier(portName);
		
						commPort = portIdentifier.open("CUL4OSGi", 2000);
		
						if (commPort instanceof SerialPort) {
							SerialPort serialPort = (SerialPort) commPort;
							serialPort.setInputBufferSize(10240);
							serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
									SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);
		
							in = serialPort.getInputStream();
							out = serialPort.getOutputStream();
						}
					}
					catch (NoClassDefFoundError ncdfe) {
						
					}
					catch (NoSuchPortException nspe) {
						System.out.print("CUL4OSGi: Error, could not open port " + portName);
					}
				}
	
				if ((in != null) && (out != null)) {
					culInterface.Open(in, out);
				}
			} else {
				System.out.println ("CUL4OSGi not using external port\r\n");
				
//				transceiver = new FS20TransceiverNull();
				
//				((FS20AbstractTransceiver)transceiver).ParseFHT("T10139502");
//				((FS20AbstractTransceiver)transceiver).ParseFHT("T194500AA00");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (culInterface != null) {
			culServiceRegistration = (ServiceRegistration<CULInterface>) (context.getBundleContext().registerService(CULInterface.class.getName(), culInterface, null));
		}
	}

	protected void deactivate(ComponentContext context) {
		if (culInterface != null) {
			if (culServiceRegistration != null)
				culServiceRegistration.unregister();
			
			if (culInterface instanceof CULTransceiver)
				((CULTransceiver)culInterface).Close();

			culInterface = null;
		}

		if (commPort != null) {
			if (commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;

				if (serialPort != null)
					serialPort.close();
			}
		}
		
		this.context = context;
	}

}
