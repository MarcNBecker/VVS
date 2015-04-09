package de.dhbw.vvs.model;

import java.util.ArrayList;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.database.ConnectionPool;
import de.dhbw.vvs.database.DatabaseConnection;
import de.dhbw.vvs.utility.TypeHashMap;
import de.dhbw.vvs.utility.Utility;

/**
 * A class to represent a User
 */
public class User {

	private String name;
	private String passwort;
	private int repraesentiert;
	
	/**
	 * Returns a list of all User
	 * @return a list of all User
	 * @throws WebServiceException
	 */
	public static ArrayList<User> getAll() throws WebServiceException {
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT name, repraesentiert FROM user ORDER BY name ASC", null);
		ArrayList<User> userList = new ArrayList<User>();
		for(TypeHashMap<String, Object> result : resultList) {
			User u = new User(result.getString("name"));
			u.repraesentiert = result.getInt("repraesentiert");
			userList.add(u);
		}
		return userList;
	}
	
	/**
	 * Constructs a user
	 * @param name the users name
	 * @throws WebServiceException
	 */
	public User(String name) throws WebServiceException {
		if (name == null || (name = name.trim()).isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
		this.name = name;
	}
	
	/**
	 * Authenticates a user by checking if the correct password was given
	 * @return the authenticated user
	 * @throws WebServiceException
	 */
	public User authenticate() throws WebServiceException {
		if (name == null || (name = name.trim()).isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
		if (passwort == null || (passwort = passwort.trim()).isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(name);
		fieldValues.add(Utility.sha256(passwort));
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT repraesentiert FROM user WHERE name = ? AND passwort = ?", fieldValues);
		if(resultList.isEmpty()) {
			throw new WebServiceException(ExceptionStatus.AUTHENTICATION_FAILED);
		}
		TypeHashMap<String, Object> result = resultList.get(0);
		repraesentiert = result.getInt("repraesentiert");
		passwort = "";
		return this;
	}
	
	/**
	 * Creates a user
	 * @return the created user
	 * @throws WebServiceException
	 */
	public User create() throws WebServiceException {
		checkDirectAttributes(false);
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(name);
		fieldValues.add(Utility.sha256(passwort));
		fieldValues.add(repraesentiert);
		db.doQuery("INSERT INTO user (name, passwort, repraesentiert) VALUES (?, ?, ?)", fieldValues);
		passwort = "";
		return this;
	}
	
	/**
	 * Gets all attributes of a user
	 * @return the user with all attributes set
	 * @throws WebServiceException
	 */
	public User getDirectAttributes() throws WebServiceException {
		if (name == null || (name = name.trim()).isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(name);
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT repraesentiert FROM user WHERE name = ?", fieldValues);
		if(resultList.isEmpty()) {
			throw new WebServiceException(ExceptionStatus.OBJECT_NOT_FOUND);
		}
		TypeHashMap<String, Object> result = resultList.get(0);
		repraesentiert = result.getInt("repraesentiert");
		passwort = "";
		return this;
	}
	
	/**
	 * Updates a user
	 * @return the updated user
	 * @throws WebServiceException
	 */
	public User update() throws WebServiceException {
		checkDirectAttributes(true);
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		String passwortString = "";
		if(passwort != null) {
			fieldValues.add(Utility.sha256(passwort));
			passwortString = "passwort = ?,";
		}
		fieldValues.add(repraesentiert);
		fieldValues.add(name);
		int affectedRows = db.doQuery("UPDATE user SET " + passwortString + " repraesentiert = ? WHERE name = ?", fieldValues);
		passwort = "";
		if(affectedRows == 0) {
			throw new WebServiceException(ExceptionStatus.OBJECT_NOT_FOUND);
		} else {
			return this;
		}
	}
	
	/**
	 * Deletes a user
	 * @throws WebServiceException
	 */
	public void delete() throws WebServiceException {
		if (name == null || (name = name.trim()).isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(name);
		db.doQuery("DELETE FROM user WHERE name = ?", fieldValues);
	}
	
	/**
	 * Checks if a users exists
	 * @throws WebServiceException if the user doesn't exist
	 */
	public void checkExistance() throws WebServiceException {
		if (name == null || (name = name.trim()).isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(name);
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT name FROM user WHERE name = ?", fieldValues);
		if(resultList.isEmpty()) {
			throw new WebServiceException(ExceptionStatus.OBJECT_NOT_FOUND);
		}
	}
	
	/**
	 * Checks all attributes of a User
	 * @param update specifies if the check is done for an Update
	 * @throws WebServiceException if an attribute is invalid
	 */
	public void checkDirectAttributes(boolean update) throws WebServiceException {
		if (name == null || (name = name.trim()).isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
		if (passwort == null || (passwort = passwort.trim()).isEmpty()) {
			if(update) {
				passwort = null;
			} else {
				throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
			}
		}
		if (repraesentiert <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		} else {
			new Studiengangsleiter(repraesentiert).getDirectAttributes(); //check existance
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getRepraesentiert() {
		return repraesentiert;
	}
}
