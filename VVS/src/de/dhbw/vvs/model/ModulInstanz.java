package de.dhbw.vvs.model;

import java.util.ArrayList;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.database.ConnectionPool;
import de.dhbw.vvs.database.DatabaseConnection;
import de.dhbw.vvs.utility.TypeHashMap;

/**
 * A class to represent a ModulInstanz
 * A ModulInstanz is a specific Modul in a Modulplan
 */
public class ModulInstanz {

	private int id;
	private Modul modul;
	private int modulplanID;
	private int credits;
	
	/**
	 * Returns a list of all ModulInstanzen in a Modulplan
	 * @param modulplan the Modulplan
	 * @return a list of all ModulInstanzen
	 * @throws WebServiceException
	 */
	public static ArrayList<ModulInstanz> getAll(Modulplan modulplan) throws WebServiceException {
		if (modulplan.getID() <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(modulplan.getID());
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT id, modul, modulplan, credits FROM modulinstanz WHERE modulplan = ?", fieldValues);
		ArrayList<ModulInstanz> modulInstanzList = new ArrayList<ModulInstanz>();
		for(TypeHashMap<String, Object> result : resultList) {
			ModulInstanz m = new ModulInstanz(result.getInt("id"));
			m.modul = new Modul(result.getInt("modul")).getDirectAttributes();
			m.modulplanID = result.getInt("modulplan");
			m.credits = result.getInt("credits");
			modulInstanzList.add(m);
		}
		return modulInstanzList;
	}
	
	/**
	 * Returns a ModulInstanz for a Modulplan and a Modul
	 * @param modulplanID the id of the Modulplan
	 * @param modulID the id of the Modul
	 * @return the ModulInstanz
	 * @throws WebServiceException
	 */
	public static ModulInstanz getSingle(int modulplanID, int modulID) throws WebServiceException {
		if (modulplanID <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		if (modulID <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(modulplanID);
		fieldValues.add(modulID);
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT id, credits FROM modulinstanz WHERE modulplan = ? AND modul = ?", fieldValues);
		if(resultList.isEmpty()) {
			throw new WebServiceException(ExceptionStatus.OBJECT_NOT_FOUND);
		}
		TypeHashMap<String, Object> result = resultList.get(0);
		ModulInstanz m = new ModulInstanz(result.getInt("id"));
		m.modul = new Modul(modulID).getDirectAttributes();
		m.modulplanID = modulplanID;
		m.credits = result.getInt("credits");
		return m;
	}
	
	/**
	 * Constructs a ModulInstanz
	 * @param id the id
	 * @throws WebServiceException
	 */
	public ModulInstanz(int id) throws WebServiceException {
		if(id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		this.id = id;
	}
	
	/**
	 * Gets all direct attributes of a ModulInstanz
	 * @return the ModulInstanz with all attributes set
	 * @throws WebServiceException
	 */
	public ModulInstanz getDirectAttributes() throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(id);
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT modul, modulplan, credits FROM modulinstanz WHERE id = ?", fieldValues);
		if(resultList.isEmpty()) {
			throw new WebServiceException(ExceptionStatus.OBJECT_NOT_FOUND);
		}
		TypeHashMap<String, Object> result = resultList.get(0);
		modul = new Modul(result.getInt("modul")).getDirectAttributes();
		modulplanID = result.getInt("modulplan");
		credits = result.getInt("credits");
		return this;
	}
	
	/**
	 * Returns a list of all FachInstanzen of a ModulInstanz
	 * @return a list of all FachInstanzen
	 * @throws WebServiceException
	 */
	public ArrayList<FachInstanz> getFachList() throws WebServiceException {
		return FachInstanz.getAll(this);
	}
	
	/**
	 * Creates a ModulInstanz
	 * @return the created ModulInstanz
	 * @throws WebServiceException
	 */
	public ModulInstanz create() throws WebServiceException {
		/*if (id != 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}*/
		if(modul.getID() == 0) {
			modul.create();
		}
		checkDirectAttributes();		
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(modul.getID());
		fieldValues.add(modulplanID);
		fieldValues.add(credits);
		fieldValues.add(credits);
		int newID = db.doQuery("INSERT INTO modulinstanz (modul, modulplan, credits) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE credits = ?", fieldValues);
		if(id <= 0) {
			id = newID;
		}
		return this;
	}
	
	/**
	 * Deletes a ModulInstanz
	 * @throws WebServiceException
	 */
	public void delete() throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		getDirectAttributes();
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(id);
		db.doQuery("DELETE FROM modulinstanz WHERE id = ?", fieldValues);
		//Also delete Modul if there is no ModulInstanz left
		if (modul.getInstanzenCount() == 0) {
			modul.delete();	
		}
	}
	
	/**
	 * Checks all direct attributes of a ModulInstanz
	 * @throws WebServiceException if an attribute is invalid
	 */
	private void checkDirectAttributes() throws WebServiceException {
		if (modul == null || modul.getID() <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_OBJECT);
		} else {
			modul.getDirectAttributes(); //check existance
		}
		if (modulplanID <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		} else {
			new Modulplan(modulplanID).getDirectAttributes(); //check existance
		}
		if (credits < 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_NUMBER);
		}
	}
	
	public int getID() {
		return id;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public void setModulplanID(int modulplanID) {
		this.modulplanID = modulplanID;
	}
	
	/**
	 * It is only allowed to call this method, when modul.setID() follows this call
	 * Or the method {@link #getDirectAttributes()} has been called before
	 */
	public Modul getModul() {
		if(modul == null) {
			try {
				return new Modul(1);
			} catch (WebServiceException e) {
				return null;
			}
		} else {
			return modul;
		}
	}
	
}
