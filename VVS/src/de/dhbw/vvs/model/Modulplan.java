package de.dhbw.vvs.model;

import java.util.ArrayList;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.database.ConnectionPool;
import de.dhbw.vvs.database.DatabaseConnection;
import de.dhbw.vvs.utility.TypeHashMap;

/**
 * A class to represent a Modulplan
 */
public class Modulplan {

	private int id;
	private String studiengang;
	private String vertiefungsrichtung;
	private int gueltigAb;
	
	/**
	 * Returns a list of all Modulplaene
	 * @return a list of all Modulplaene
	 * @throws WebServiceException
	 */
	public static ArrayList<Modulplan> getAll() throws WebServiceException {
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT id, studiengang, vertiefungsrichtung, gueltigAb FROM modulplan ORDER BY studiengang ASC", null);
		ArrayList<Modulplan> modulplanList = new ArrayList<Modulplan>();
		for(TypeHashMap<String, Object> result : resultList) {
			Modulplan m = new Modulplan(result.getInt("id"));
			m.studiengang = result.getString("studiengang");
			m.vertiefungsrichtung = result.getString("vertiefungsrichtung");
			m.gueltigAb = result.getInt("gueltigAb");
			modulplanList.add(m);
		}
		return modulplanList;
	}
	
	/**
	 * Constructs a Modulplan
	 * @param id the id
	 * @throws WebServiceException
	 */
	public Modulplan(int id) throws WebServiceException {
		if(id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		this.id = id;
	}
	
	/**
	 * Gets all direct attributes of a Modulplan
	 * @return the Modulplan with all attributes set
	 * @throws WebServiceException
	 */
	public Modulplan getDirectAttributes() throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(id);
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT studiengang, vertiefungsrichtung, gueltigAb FROM modulplan WHERE id = ?", fieldValues);
		if(resultList.isEmpty()) {
			throw new WebServiceException(ExceptionStatus.OBJECT_NOT_FOUND);
		}
		TypeHashMap<String, Object> result = resultList.get(0);
		studiengang = result.getString("studiengang");
		vertiefungsrichtung = result.getString("vertiefungsrichtung");
		gueltigAb = result.getInt("gueltigAb");
		return this;
	}
	
	/**
	 * Returns a list of all ModulInstanzen for a Modulplan
	 * @return a list of all ModulInstanzen for a Modulplan
	 * @throws WebServiceException
	 */
	public ArrayList<ModulInstanz> getModulList() throws WebServiceException {
		return ModulInstanz.getAll(this);
	}
	
	/**
	 * Creates a Modulplan
	 * @return the created Modulplan
	 * @throws WebServiceException
	 */
	public Modulplan create() throws WebServiceException {
		if (id != 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		checkDirectAttributes();
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(studiengang);
		fieldValues.add(vertiefungsrichtung);
		fieldValues.add(gueltigAb);
		this.id = db.doQuery("INSERT INTO modulplan (studiengang, vertiefungsrichtung, gueltigAb) VALUES (?, ?, ?)", fieldValues);
		return this;
	}
	
	/**
	 * Copies a Modulplan to the current Modulplan
	 * @param vorlageID the id of the Modulplan to copy
	 * @throws WebServiceException
	 */
	public void enhance(int vorlageID) throws WebServiceException {
		Modulplan vorlage = new Modulplan(vorlageID).getDirectAttributes();
		ArrayList<ModulInstanz> modulInstanzList = vorlage.getModulList();
		for(ModulInstanz modulInstanz: modulInstanzList) {
			ArrayList<FachInstanz> fachInstanzList = FachInstanz.getAll(modulInstanz);
			modulInstanz.setModulplanID(id);
			modulInstanz.setID(0);
			modulInstanz.create();
			for(FachInstanz fachInstanz: fachInstanzList) {
				fachInstanz.setModulInstanzID(modulInstanz.getID());
				fachInstanz.setID(0);
				fachInstanz.create();
			}
		}
	}
	
	/**
	 * Updates a Modulplan
	 * @return the updated Modulplan
	 * @throws WebServiceException
	 */
	public Modulplan update() throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		checkDirectAttributes();
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(studiengang);
		fieldValues.add(vertiefungsrichtung);
		fieldValues.add(gueltigAb);
		fieldValues.add(id);
		int affectedRows = db.doQuery("UPDATE modulplan SET studiengang = ?, vertiefungsrichtung = ?, gueltigAb = ? WHERE id = ?", fieldValues);
		if(affectedRows == 0) {
			throw new WebServiceException(ExceptionStatus.OBJECT_NOT_FOUND);
		} else {
			return this;	
		}
	}
	
	/**
	 * Deletes a Modulplan
	 * @throws WebServiceException
	 */
	public void delete() throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(id);
		db.doQuery("DELETE FROM modulplan WHERE id = ?", fieldValues);
	}
	
	/**
	 * Checks all attributes of a Modulplan
	 * @throws WebServiceException if an attribute is invalid
	 */
	private void checkDirectAttributes() throws WebServiceException {
		if (studiengang == null || (studiengang = studiengang.trim()).isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
		if (vertiefungsrichtung == null || (vertiefungsrichtung = vertiefungsrichtung.trim()).isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
		if (gueltigAb < 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_NUMBER);
		}
	}
	
	public int getID() {
		return id;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public String getStudiengang() {
		return studiengang;
	} 
	
}
