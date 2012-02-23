package de.tobiaswegner.communication.cul4java.osgi;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import de.tobiaswegner.communication.cul4java.CULInterface;

public class CULCommandProvider implements CommandProvider {
	protected CULInterface culInterface;
	
	public CULCommandProvider()
	{
	}
	
	public void bindCULInterface (CULInterface culInterface) {
		this.culInterface = culInterface;
	}
	
	public void unbindCULInterface (CULInterface culInterface) {
		this.culInterface = culInterface;
	}
	
	public String getHelp() {
		return "---CUL commands---\r\n" + 
				"\tfs20 help - help on fs20 command\r\n";
	}
	
	public void _cul(CommandInterpreter ci) {
		String cmd = ci.nextArgument();
		
		String arg = ci.nextArgument();
		
		if (arg == null) {
			culInterface.Decode(cmd);
			
			return;
		}
		
		if (cmd.equals("send")) {
			culInterface.RAW_Send(arg + "\r\n");
		}
	}

	public void _fs20(CommandInterpreter ci) {
		String cmd = ci.nextArgument();
		
		if (cmd == null)
			cmd = "help";
		
		if (cmd.equals("send")) {
			String HouseCode = ci.nextArgument();
			String Address = ci.nextArgument();
			String Command = ci.nextArgument();
			
			if ((HouseCode == null) || (Address == null) || (Command == null))
				ci.println("You have to specify housecode, adress and command");
			else
				culInterface.FS20_Send(HouseCode, Address, Command);
		}
		
		if (cmd.equals("help")) {
			ci.println("fs20 send <housecode> <address> <command> - You have to specify housecode, adress and command");
		}
	}

	public void _fht(CommandInterpreter ci) {
		String cmd = ci.nextArgument();
		
		if (cmd.equals("set_temp")) {
			String device = ci.nextArgument();
			
			String temp = ci.nextArgument();
			
			double t = Double.parseDouble(temp);
			
			culInterface.FHT_SetDesiredTemperature(device, t);
		}
	}
	
	public void _T(CommandInterpreter ci) {
		String arg = ci.nextArgument();
		//transceiver.FHT_Send_RAW(arg);
	}
}
