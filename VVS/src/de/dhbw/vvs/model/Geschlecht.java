package de.dhbw.vvs.model;

import java.util.EnumSet;
import java.util.HashMap;

public enum Geschlecht implements WebServiceEnum {
	
	HERR(0), FRAU(1);
	
	private static HashMap<Integer, Geschlecht> mapping = new HashMap<Integer, Geschlecht>();

	static {
		//Do custom mapping, to avoid any problems with mixing up the numbers with their category, 
		//since the numbers are used to store the enum value in the database
		for (Geschlecht cat : EnumSet.allOf(Geschlecht.class)) {
			mapping.put(cat.value, cat);
		}
	}

	private int	value;

	/**
	 * Constructs a enum
	 * @param value the integer representation of the enum
	 */
	private Geschlecht(int value) {
		this.value = value;
	}
	
	/**
	 * Retrieves a enum by it's integer representation
	 * @param ordinal the integer representation of the enum
	 * @return the enum object
	 */
	public static Geschlecht getFromOrdinal(Integer ordinal) {
		return mapping.get(ordinal);
	}
	
	@Override
	public int getValue() {
		return value;
	}
	
	/**
	 * @returns a string representation of the integer representation of the enum
	 */
	@Override
	public String toString() {
		return this.value+"";
	}
}
