package de.dhbw.vvs.model;

import java.util.ArrayList;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.database.ConnectionPool;
import de.dhbw.vvs.database.DatabaseConnection;
import de.dhbw.vvs.utility.TypeHashMap;
import de.dhbw.vvs.utility.Utility;

public class User {

	private String name;
	private String passwort;
	private int repraesentiert;
	
	public User(String name) throws WebServiceException {
		if (name == null || (name = name.trim()).isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
		this.name = name;
	}
	
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
	
	public User create() throws WebServiceException {
		checkDirectAttributes();
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(name);
		fieldValues.add(Utility.sha256(passwort));
		fieldValues.add(repraesentiert);
		db.doQuery("INSERT INTO user (name, passwort, repraesentiert) VALUES (?, ?, ?)", fieldValues);
		passwort = "";
		return this;
	}
	
	public User update() throws WebServiceException {
		checkDirectAttributes();
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(Utility.sha256(passwort));
		fieldValues.add(repraesentiert);
		fieldValues.add(name);
		int affectedRows = db.doQuery("UPDATE user SET passwort = ?, repraesentiert = ? WHERE name = ?", fieldValues);
		passwort = "";
		if(affectedRows == 0) {
			throw new WebServiceException(ExceptionStatus.OBJECT_NOT_FOUND);
		} else {
			return this;
		}
	}
	
	public void delete() throws WebServiceException {
		if (name == null || (name = name.trim()).isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(name);
		db.doQuery("DELETE FROM user WHERE name = ?", fieldValues);
	}
	
	public void checkDirectAttributes() throws WebServiceException {
		if (name == null || (name = name.trim()).isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
		if (passwort == null || (passwort = passwort.trim()).isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
		if (repraesentiert <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		} else {
			new Studiengangsleiter(repraesentiert).getDirectAttributes(); //check existance
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
