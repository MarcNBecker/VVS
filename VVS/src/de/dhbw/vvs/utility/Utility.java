package de.dhbw.vvs.utility;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.sql.Time;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;

/**
 * A utility class
 */
public class Utility {
	
	public static final String DATE_STRING = "yyyy-MM-dd";
	public static final String TIME_STRING = "HH:mm:ss";
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_STRING);
	private static final DateFormat TIME_FORMAT = new SimpleDateFormat(TIME_STRING);
	
	/**
	 * Generates a highly cryptographical 128 char long hexadecimal token
	 * @return the token
	 */
	public static String generateToken() {
		//This makes the token exactly 128 hexadecimal chars long
		return new BigInteger(512, new SecureRandom()).toString(16);
	}
	
	/**
	 * Hashes a string using the sha256 algorithm
	 * @param s the string to be hashed
	 * @return the hashed string
	 * @throws WebServiceException if the hashing process fails
	 */
	public static String sha256(String s) throws WebServiceException {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashedString = digest.digest(s.getBytes("UTF-8"));
			return Base64.getUrlEncoder().encodeToString(hashedString);
		} catch (Exception e) {
			throw new WebServiceException(ExceptionStatus.SHA256_FAILED);
		}
	}		
	
	/**
	 * Checks a string against an email regex
	 * @param email the string to be validated
	 * @return true if the string is a valid email
	 */
	public static boolean checkEmail(String email) {
		return email.matches(("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"));
	}
	
	/**
	 * Checks a string against the phone number library
	 * @param phoneNumber the string to be validated
	 * @return true if the string is a valid phone number
	 */
	public static boolean checkPhone(String phoneNumber) {
		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
		try {
			return phoneUtil.isValidNumber(phoneUtil.parse(phoneNumber, null));	
		} catch (NumberParseException e) {
			return false;
		}
	}
	
	/**
	 * Checks if a string only contains numbers
	 * @param s the string to be checked
	 * @return true if the string only contains numbers
	 */
	public static boolean checkNumeric(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	/**
	 * Converts a date object to a string representation as specified in DATE_STRING
	 * @param d the date object
	 * @return the string representation
	 */
	public static String dateString(Date d) {
		return DATE_FORMAT.format(d);
	}
	
	/**
	 * Converts a date object to a string representation as specified in TIME_STRING
	 * @param d the date object
	 * @return the string representation
	 */
	public static String timeString(Time t) {
		return TIME_FORMAT.format(t);
	}
	
	/**
	 * Returns a date object constructed based on a string date representation as specified in DATE_STRING
	 * @param s the string containing a date according to DATE_STRING
	 * @return the date object
	 * @throws WebServiceException if the parsing fails
	 */
	public static Date stringDate(String s) throws WebServiceException {
		try {
			return new Date(DATE_FORMAT.parse(s).getTime());
		} catch (ParseException e) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_DATE);
		}
	}
	
	/**
	 * Returns a time object constructed based on a string time representation as specified in TIME_STRING
	 * @param s the string containing a time according to TIME_STRING
	 * @return the time object
	 * @throws WebServiceException if the parsing fails
	 */
	public static Time stringTime(String s) throws WebServiceException {
		try {
			return new Time(TIME_FORMAT.parse(s).getTime());
		} catch (ParseException e) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_DATE);
		}
	}
	
}
