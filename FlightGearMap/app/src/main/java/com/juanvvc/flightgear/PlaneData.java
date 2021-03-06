package com.juanvvc.flightgear;

import java.util.Date;

/** Models the data that FlightGear sends.
 * @author juanvi
 *
 */
public class PlaneData {
	String[] data;
	String[] outData;
	private Date date = new Date();
	private MovingAverage[] averages;
	
	public PlaneData() {
		data = null;
		this.averages = new MovingAverage[ALTITUDE_AGL+1];
	}
	
	public void parse(final String input) {
		// strip string with new line
		String realInput = input.substring(0, input.indexOf("\n"));
		data = realInput.split(":");
		
		date = new Date();
		
		// check that we have the desired number of parameters
		// just read the last data. If throws IndexOutOfBounds, the
		// other extreme is sending wrong data
		getFloat(ALTITUDE_AGL);
	}
	
	public static final int SPEED = 0; // speed, in knots
	public static final int RPM = 1; // RPM
	public static final int HEADING_MOV = 2; // Magnetic heading, in degrees
	public static final int ALTITUDE = 3; // altitude, in feet, according to the instruments
	public static final int CLIMB_RATE = 4; // rate of climb, in feet per second
	public static final int PITCH = 5; // pitch, in degrees
	public static final int ROLL = 6; // roll, in degrees
	public static final int LATITUDE = 7; // latitude, in degrees
	public static final int LONGITUDE = 8; // longitude, in degrees
	public static final int SECONDS = 9; // seconds from GMT midnight
	public static final int TURN_RATE = 10; // turn rate, in turns per 2min
	public static final int SLIP = 11; // slip skid, in ??
	public static final int HEADING = 12; // Heading in degrees, according to the instruments
	public static final int FUEL1 = 13; // fuel in first tank, in us gals
	public static final int FUEL2 = 14; // fuel in second tank, in us gals
	public static final int OIL_PRESS = 15; // oil pressure in psi
	public static final int OIL_TEMP = 16; // oil temperature in degf
	public static final int AMP = 17; // amperes
	public static final int VOLT = 18; // voltage
	public static final int NAV1_TO = 19; // true if the to flag is set in NAV1
	public static final int NAV1_FROM = 20; // true if the from flag is set in NAV1
	public static final int NAV1_DEFLECTION = 21; // needle deflection in NAV1
	public static final int NAV1_SEL_RADIAL = 22; // selected radial in NAV1
	public static final int NAV2_TO = 23; // true if the to flag is set in NAV2
	public static final int NAV2_FROM = 24; // true if the from flag is set in NAV2
	public static final int NAV2_DEFLECTION = 25; // needle deflection in NAV2
	public static final int NAV2_SEL_RADIAL = 26; // selected radial in NAV2
	public static final int ADF_DEFLECTION = 27;
	public static final int ELEV_TRIM = 28;
	public static final int FLAPS = 29;
	public static final int GS1_DEFLECTION = 30; // normalized needle deflection (only NAV1)
	public static final int GS1_INRANGE = 31; // true if GS in range (only NAV1)
	public static final int DME = 32;
	public static final int DME_SPEED = 33;
	public static final int RPM2 = 34;
	public static final int MANIFOLD = 35;
	public static final int MANIFOLD2 = 36;
	public static final int CHT1_TEMP = 37;
	public static final int CHT2_TEMP = 38;
	public static final int OIL2_PRESS = 39;
	public static final int OIL2_TEMP = 40;
	public static final int HEADING_BUG = 41;
	public static final int NAV1_HEADING = 42; // heading to the NAV station
	public static final int ALTITUDE_AGL = 43;
	
	// These are used in the B1900D, a turboprop. Notice that indexes are repeated!
	public static final int FUEL_FLOW1 = CHT1_TEMP;
	public static final int FUEL_FLOW2 = CHT2_TEMP;
	public static final int PROP_ENGINE1 = MANIFOLD;
	public static final int PROP_ENGINE2 = MANIFOLD2;
	public static final int TORQUE1 = RPM;
	public static final int TORQUE2 = RPM2;
	public static final int TURBINE1 = AMP;
	public static final int TURBINE2 = VOLT;
	public static final int VNE_SPEED = ALTITUDE_AGL;
	public static final int NAV2_HEADING = NAV1_HEADING;
	// since oil press doesn't seem to be right on the b1900d, use the slot of ITT
	public static final int ITT1 = OIL_PRESS;
	public static final int ITT2 = OIL2_PRESS;
	

	
	public int getInt(int i) {
		if (data == null) {
			return 0;
		}
		return Integer.valueOf(data[i]);
	}
	
	public float getFloat(int i) {
		if (data == null) {
			return 0;
		}
		MovingAverage ma = this.averages[i];
		if (ma==null) {
			return Float.valueOf(data[i]);
		} else {
			return ma.getData(Float.valueOf(data[i]));
		}
	}
	
	public String getString(int i) {
		if (data == null) {
			return "";
		}
		return data[i];
	}
	
	public boolean getBool(int i) {
		if (data == null) {
			return false;
		}
		return data[i].equals("1");
	}
	
	public Date getDate() {
		return date;
	}

	public boolean hasData() {
		return data != null;
	}
	
	/**
	 * Sets or removes a MovingAverage filter for a specific index
	 * @param index The index of data to set the filter
	 * @param set set or remove the filter.
	 */
	public void setMovingAverage(int index, boolean set) {
		if (set) {
			this.averages[index] = new MovingAverage();
		} else {
			this.averages[index] = null;
		}
	}
	
	/** This filter smoothes the data to prevent very fast changes */
	private class MovingAverage {
		private static final int SIZE=5;
		private float[] buffer = null;
		private int current = 0;
		
		public float getData(float v) {
			if ( buffer == null) {
				buffer = new float[SIZE];
				for(int i=0; i<SIZE; i++) buffer[i]=v;
				return v;
			}
			float res = 0;
			buffer[current] = v;
			for(int i=0; i<SIZE; i++) res+=buffer[i];
			current = (current + 1) % SIZE;
			return res/SIZE;
		}
	}
}
