package de.dhbw.vvs.model;

import java.util.ArrayList;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.database.ConnectionPool;
import de.dhbw.vvs.database.DatabaseConnection;
import de.dhbw.vvs.utility.TypeHashMap;

/**
 * A class to represent a Studiengangsleiter
 */
public class Studiengangsleiter {
	
	private int id;
	private String name;
	
	/**
	 * Returns a list of all Studiengangsleiter
	 * @return the list of all Studiengangsleiter
	 * @throws WebServiceException
	 */
	public static ArrayList<Studiengangsleiter> getAll() throws WebServiceException {
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT id, name FROM studiengangsleiter ORDER BY name ASC", null);
		ArrayList<Studiengangsleiter> studiengangsleiterList = new ArrayList<Studiengangsleiter>();
		for(TypeHashMap<String, Object> result : resultList) {
			Studiengangsleiter s = new Studiengangsleiter(result.getInt("id"));
			s.name = result.getString("name");
			studiengangsleiterList.add(s);
		}
		return studiengangsleiterList;
	}
	
	/**
	 * Constructs a Studiengangsleiter
	 * @param id the id
	 * @throws WebServiceException
	 */
	public Studiengangsleiter(int id) throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		this.id = id;
	}
	
	/**
	 * Gets all direct attributes of the Studiengangsleiter
	 * @return the Studiengangsleiter with all attributes set
	 * @throws WebServiceException
	 */
	public Studiengangsleiter getDirectAttributes() throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(id);
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT name FROM studiengangsleiter WHERE id = ?", fieldValues);
		if(resultList.isEmpty()) {
			throw new WebServiceException(ExceptionStatus.OBJECT_NOT_FOUND);
		}
		TypeHashMap<String, Object> result = resultList.get(0);
		name = result.getString("name");
		return this;
	}
	
	/**
	 * Creates a Studiengangsleiter
	 * @return the created studiengangsleiter
	 * @throws WebServiceException
	 */
	public Studiengangsleiter create() throws WebServiceException {
		if (id != 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		checkDirectAttributes();
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(name);
		this.id = db.doQuery("INSERT INTO studiengangsleiter (name) VALUES (?)", fieldValues);
		return this;
	}
	
	/**
	 * Sets the User, this Studiengangsleiter is representing
	 * @param user the user
	 * @throws WebServiceException
	 */
	public void setIst(User user) throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}	
		if (user.getName() == null || user.getName().trim().isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(user.getName().trim());
		fieldValues.add(id);
		int affectedRows = db.doQuery("UPDATE studiengangsleiter SET ist = ? WHERE id = ?", fieldValues);
		if(affectedRows == 0) {
			throw new WebServiceException(ExceptionStatus.OBJECT_NOT_FOUND);
		}
	}
	
	/**
	 * Updates a Studiengangsleiter
	 * @return the updated Studiengangsleiter
	 * @throws WebServiceException
	 */
	public Studiengangsleiter update() throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		checkDirectAttributes();
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(name);
		fieldValues.add(id);
		int affectedRows = db.doQuery("UPDATE studiengangsleiter SET name = ? WHERE id = ?", fieldValues);
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
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(id);
		db.doQuery("DELETE FROM studiengangsleiter WHERE id = ?", fieldValues);
	}
	
	/**
	 * Checks all attributes of a user
	 * @throws WebServiceException if an attribute is invalid
	 */
	public void checkDirectAttributes() throws WebServiceException {
		if (name == null || (name = name.trim()).isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
	}
	
	public int getID() {
		return id;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
}
