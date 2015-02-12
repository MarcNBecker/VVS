package de.dhbw.vvs.model;

/**
 * Marker interface for Enums used in the WebService model
 * This marker is used by the JSON deserializer
 * @author Marc Becker
 */
public interface WebServiceEnum {

	/**
	 * @return the database consistent value of the enum
	 */
	public int getValue();
	
}
