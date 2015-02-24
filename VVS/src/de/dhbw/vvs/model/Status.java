package de.dhbw.vvs.model;

import java.util.EnumSet;
import java.util.HashMap;

public enum Status implements WebServiceEnum {
	
	Neu(0), Aktiv(1), Inaktiv(2);
	
	private static HashMap<Integer, Status> mapping = new HashMap<Integer, Status>();

	static {
		//Do custom mapping, to avoid any problems with mixing up the numbers with their category, 
		//since the numbers are used to store the enum value in the database
		for (Status cat : EnumSet.allOf(Status.class)) {
			mapping.put(cat.value, cat);
		}
	}

	private int	value;

	/**
	 * Constructs a enum
	 * @param value the integer representation of the enum
	 */
	private Status(int value) {
		this.value = value;
	}
	
	/**
	 * Retrieves a enum by it's integer representation
	 * @param ordinal the integer representation of the enum
	 * @return the enum object
	 */
	public static Status getFromOrdinal(Integer ordinal) {
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
