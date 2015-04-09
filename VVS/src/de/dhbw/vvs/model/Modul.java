package de.dhbw.vvs.model;

import java.util.ArrayList;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.database.ConnectionPool;
import de.dhbw.vvs.database.DatabaseConnection;
import de.dhbw.vvs.utility.TypeHashMap;

/**
 * A class to represent a Modul
 */
public class Modul {

	private int id;
	private String name; 
	private String kurzbeschreibung;
	
	/**
	 * Returns a list of all Module
	 * @return a list of all Module
	 * @throws WebServiceException
	 */
	public static ArrayList<Modul> getAll() throws WebServiceException {
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT id, name, kurzbeschreibung FROM modul ORDER BY name ASC", null);
		ArrayList<Modul> modulList = new ArrayList<Modul>();
		for(TypeHashMap<String, Object> result : resultList) {
			Modul m = new Modul(result.getInt("id"));
			m.name = result.getString("name");
			m.kurzbeschreibung = result.getString("kurzbeschreibung");
			modulList.add(m);
		}
		return modulList;
	}
	
	/**
	 * Constructs a Modul
	 * @param id the id
	 * @throws WebServiceException
	 */
	public Modul(int id) throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		this.id = id;
	}
	
	/**
	 * Gets all direct attributes of a Modul
	 * @return the modul with all direct attributes set
	 * @throws WebServiceException
	 */
	public Modul getDirectAttributes() throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(id);
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT name, kurzbeschreibung FROM modul WHERE id = ?", fieldValues);
		if(resultList.isEmpty()) {
			throw new WebServiceException(ExceptionStatus.OBJECT_NOT_FOUND);
		}
		TypeHashMap<String, Object> result = resultList.get(0);
		name = result.getString("name");
		kurzbeschreibung = result.getString("kurzbeschreibung");
		return this;
	}
	
	/**
	 * Creates a Modul
	 * @return the created modul
	 * @throws WebServiceException
	 */
	public Modul create() throws WebServiceException {
		if (id != 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		checkDirectAttributes();
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(name);
		fieldValues.add(kurzbeschreibung);
		this.id = db.doQuery("INSERT INTO modul (name, kurzbeschreibung) VALUES (?, ?)", fieldValues);
		return this;
	}
	
	/**
	 * Deletes a Modul
	 * @throws WebServiceException
	 */
	public void delete() throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(id);
		db.doQuery("DELETE FROM modul WHERE id = ?", fieldValues);
	}
	
	/**
	 * Checks all direct attributes of a Modul
	 * @throws WebServiceException if an attributes is invalid
	 */
	private void checkDirectAttributes() throws WebServiceException {
		if (name == null || (name = name.trim()).isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
		if (kurzbeschreibung == null || (kurzbeschreibung = kurzbeschreibung.trim()).isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
	}
	
	/**
	 * Returns the number of ModulInstanzen that exist for a Modul
	 * @return the number of ModulInstanzen
	 * @throws WebServiceException
	 */
	int getInstanzenCount() throws WebServiceException {
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(id);
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT COUNT(*) AS c FROM modulinstanz WHERE modul = ?", fieldValues);
		if(resultList.isEmpty()) {
			return 0;
		}
		return resultList.get(0).getInt("c");
	}
	
	public int getID() {
		return id;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
}
