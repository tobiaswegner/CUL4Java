package de.tobiaswegner.communication.cul4java.impl;

public class FHTConstants {
	public static final int FHT_ACTUATOR_0	= 0x00;
	public static final int FHT_ACTUATOR_1	= 0x01;
	public static final int FHT_ACTUATOR_2	= 0x02;
	public static final int FHT_ACTUATOR_3	= 0x03;
	public static final int FHT_ACTUATOR_4	= 0x04;
	public static final int FHT_ACTUATOR_5	= 0x05;
	public static final int FHT_ACTUATOR_6	= 0x06;
	public static final int FHT_ACTUATOR_7	= 0x07;
	public static final int FHT_ACTUATOR_8	= 0x08;
	
	public static final int FHT_MODE = 0x3e;

	public static final int FHT_DESIRED_TEMP = 0x41;
	public static final int FHT_MEASURED_TEMP_LOW = 0x42;
	public static final int FHT_MEASURED_TEMP_HIGH = 0x43;
	
	public static String getIDByCode(int code) {
		if (code == FHT_ACTUATOR_0)
			return "actuator0";
		if (code == FHT_ACTUATOR_1)
			return "actuator1";
		if (code == FHT_ACTUATOR_2)
			return "actuator2";
		if (code == FHT_ACTUATOR_3)
			return "actuator3";
		if (code == FHT_ACTUATOR_4)
			return "actuator4";
		if (code == FHT_ACTUATOR_5)
			return "actuator5";
		if (code == FHT_ACTUATOR_6)
			return "actuator6";
		if (code == FHT_ACTUATOR_7)
			return "actuator7";
		if (code == FHT_ACTUATOR_8)
			return "actuator8";
		
		if (code == FHT_MODE)
			return "mode";

		if (code == FHT_DESIRED_TEMP)
			return "desired-temp";
		if (code == 0x42)
			return "measured-low";
		if (code == 0x43)
			return "measured-high";
		
		if (code == 0x82)
			return "day-temp";
		if (code == 0x84)
			return "night-temp";
		if (code == 0x85)
			return "lowtemp-offset";
		if (code == 0x8a)
			return "windowopen-temp";

		return "unknown";
	}
	
	public static final int FHT8b_STATE_WINDOW_OPEN = 1;
	public static final int FHT8b_STATE_WINDOW_CLOSED = 2;
	public static final int FHT8b_STATE_LOWBAT = 9;
}
