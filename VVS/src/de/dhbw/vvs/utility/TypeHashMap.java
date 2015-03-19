package de.dhbw.vvs.utility;

import java.math.BigInteger;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;

/**
 * Extended HashMap that has type aware functionality to extract String, long, int and boolean values
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class TypeHashMap <K, V> extends java.util.HashMap<K, V> {

	private static final long serialVersionUID = 6982886273544777636L;

	@SuppressWarnings("unchecked")
	@Override
	public V put(K key, V value) {
		if( key instanceof String ) {
			return super.put( (K) key.toString().toUpperCase() , value);
		} else {
			return super.put(key, value);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		if(key == null) {
			return super.get(key);
		}
		if( key instanceof String ) {
			return super.get( (K) key.toString().toUpperCase());
		} else {
			return super.get(key);
		}
	}
	
	/**
	 * Returns an integer representation of the value
	 * @param key the key to identify the value
	 * @return an integer representation of the value
	 * @throws WebServiceException if the map contains no value for the key or the value can't be cast to int
	 */
	public int getInt(Object key) throws WebServiceException {
		V value = get(key);
		if(value == null) {
			return 0; //In this project we use int for IDs..any null ID should return 0
		}
		if (value instanceof BigInteger) {
			return ((BigInteger) value).intValue();
		}
		if (value instanceof Long) {
			return ((Long) value).intValue();
		}
		if (value instanceof Integer) {
			return ((Integer) value).intValue();
		}
		if (value instanceof Boolean) {
			if((Boolean) value.equals(Boolean.TRUE)) {
				return 1;
			} else {
				return 0;
			}
		}
		throw new WebServiceException(ExceptionStatus.CASTING_INT_FAILED);
	}
	
	/**
	 * Returns a long representation of the value
	 * @param key the key to identify the value
	 * @return a long representation of the value
	 * @throws WebServiceException if the map contains no value for the key or the value can't be cast to long
	 */
	public long getLong(Object key) throws WebServiceException {
		V value = get(key);
		if(value == null) {
			throw new WebServiceException(ExceptionStatus.CASTING_NULL);
		}
		if (value instanceof BigInteger) {
			return ((BigInteger) value).longValue();
		}
		if (value instanceof Long) {
			return ((Long) value).longValue();
		}
		if (value instanceof Integer) {
			return ((Integer) value).longValue();
		}
		throw new WebServiceException(ExceptionStatus.CASTING_LONG_FAILED);
	}
	
	/**
	 * Returns a boolean representation of the value
	 * @param key the key to identify the value
	 * @return a boolean representation of the value
	 * @throws WebServiceException if the map contains no value for the key or the value can't be cast to boolean
	 */
	public boolean getBoolean(Object key) throws WebServiceException {
		V value = get(key);
		if(value == null) {
			throw new WebServiceException(ExceptionStatus.CASTING_NULL);
		}
		if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue();
		}
		throw new WebServiceException(ExceptionStatus.CASTING_BOOLEAN_FAILED);
	}
	
	/**
	 * Returns a string representation of the value
	 * @param key the key to identify the value
	 * @return a string representation of the value or null if the key is not available or the value is no string
	 * @throws WebServiceException if the map contains no value for the key or the value can't be cast to String
	 */
	public String getString(Object key) throws WebServiceException {
		V value = get(key);
		if(value == null) {
			throw new WebServiceException(ExceptionStatus.CASTING_NULL);
		}
		if(value instanceof String) {
			return (String) value;
		}
		throw new WebServiceException(ExceptionStatus.CASTING_STRING_FAILED);
	}
	
}
