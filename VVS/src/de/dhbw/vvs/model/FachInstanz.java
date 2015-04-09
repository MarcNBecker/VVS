package de.dhbw.vvs.model;

import java.util.ArrayList;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.database.ConnectionPool;
import de.dhbw.vvs.database.DatabaseConnection;
import de.dhbw.vvs.utility.TypeHashMap;

/**
 * A class representing a FachInstanz
 * A FachInstanz is a Fach that occurs in a ModulInstanz
 * It carries information about Semester and Stunden
 */
public class FachInstanz {

	private int id;
	private Fach fach;
	private int modulInstanzID;
	private int semester;
	private int stunden;
	
	/**
	 * Returns a list of all FachInstanzen that are connected to a ModulInstanz
	 * @param modulInstanz the ModulInstanz
	 * @return a list of all FachInstanzen
	 * @throws WebServiceException
	 */
	public static ArrayList<FachInstanz> getAll(ModulInstanz modulInstanz) throws WebServiceException {
		if (modulInstanz.getID() <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(modulInstanz.getID());
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT id, fach, modulInstanz, semester, stunden FROM fachinstanz WHERE modulInstanz = ?", fieldValues);
		ArrayList<FachInstanz> fachInstanzList = new ArrayList<FachInstanz>();
		for(TypeHashMap<String, Object> result : resultList) {
			FachInstanz f = new FachInstanz(result.getInt("id"));
			f.fach = new Fach(result.getInt("fach")).getDirectAttributes();
			f.modulInstanzID = result.getInt("modulInstanz");
			f.semester = result.getInt("semester");
			f.stunden = result.getInt("stunden");
			fachInstanzList.add(f);
		}
		return fachInstanzList;
	}
	
	/**
	 * Returns a list of all FachInstanzen that are in the Modulplan of the Kurs and were not used in any Vorlesung yet
	 * @param kurs the kurs
	 * @return a list of all FachInstanzen not used in any Vorlesung yet
	 * @throws WebServiceException
	 */
	public static ArrayList<FachInstanz> getAllMissingForKurs(Kurs kurs) throws WebServiceException {
		if (kurs == null) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT);
		}
		kurs.getDirectAttributes(); //check existance
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(kurs.getID());
		fieldValues.add(kurs.getModulplanID());
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT id, fach, modulInstanz, semester, stunden FROM fachinstanz WHERE id NOT IN (SELECT fachInstanz FROM vorlesung WHERE kurs = ?) AND modulInstanz IN (SELECT id FROM modulInstanz WHERE modulplan = ?) AND semester > 0", fieldValues);
		ArrayList<FachInstanz> fachInstanzList = new ArrayList<FachInstanz>();
		for(TypeHashMap<String, Object> result : resultList) {
			FachInstanz f = new FachInstanz(result.getInt("id"));
			f.fach = new Fach(result.getInt("fach")).getDirectAttributes();
			f.modulInstanzID = result.getInt("modulInstanz");
			f.semester = result.getInt("semester");
			f.stunden = result.getInt("stunden");
			fachInstanzList.add(f);
		}
		return fachInstanzList;
	}
	
	/**
	 * Returns a list of FachInstanzen that connect to a ModulInstanz using the Sondertermine Modul (Modul-ID: 40) and are not used in the Semester yet
	 * These are the only FachInstanzen that the system suggests to use in every Semester as a Vorlesung
	 * @param kurs the Kurs
	 * @param semester the semester
	 * @return a list of FachInstanzen that connect to the Sondertermine Modul
	 * @throws WebServiceException
	 */
	public static ArrayList<FachInstanz> getAllSondertermineForKurs(Kurs kurs, int semester) throws WebServiceException {
		if (kurs == null) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_OBJECT);
		}
		if (semester <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_NUMBER);
		}
		kurs.getDirectAttributes(); //check existance
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(kurs.getModulplanID());
		fieldValues.add(kurs.getID());
		fieldValues.add(semester);
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT id, fach, modulInstanz, semester, stunden FROM fachinstanz WHERE modulInstanz IN (SELECT id FROM modulInstanz WHERE modulplan = ? AND modul = 40) AND id NOT IN (SELECT fachInstanz FROM vorlesung WHERE kurs = ? AND semester = ?) AND semester = 0", fieldValues);
		ArrayList<FachInstanz> fachInstanzList = new ArrayList<FachInstanz>();
		for(TypeHashMap<String, Object> result : resultList) {
			FachInstanz f = new FachInstanz(result.getInt("id"));
			f.fach = new Fach(result.getInt("fach")).getDirectAttributes();
			f.modulInstanzID = result.getInt("modulInstanz");
			f.semester = result.getInt("semester");
			f.stunden = result.getInt("stunden");
			fachInstanzList.add(f);
		}
		return fachInstanzList;
	}
	
	/**
	 * Gets a specific FachInstanz that connects to a given ModulInstanz and connects to a given Fach
	 * @param modulInstanz the ModulInstanz
	 * @param fachID the id of a Fach
	 * @return the FachInstanz
	 * @throws WebServiceException
	 */
	public static FachInstanz getSingle(ModulInstanz modulInstanz, int fachID) throws WebServiceException {
		if (modulInstanz.getID() <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		if (fachID <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(modulInstanz.getID());
		fieldValues.add(fachID);
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT id, semester, stunden FROM fachinstanz WHERE modulInstanz = ? AND fach = ?", fieldValues);
		if(resultList.isEmpty()) {
			throw new WebServiceException(ExceptionStatus.OBJECT_NOT_FOUND);
		}
		TypeHashMap<String, Object> result = resultList.get(0);
		FachInstanz f = new FachInstanz(result.getInt("id"));
		f.fach = new Fach(fachID).getDirectAttributes();
		f.modulInstanzID = modulInstanz.getID();
		f.semester = result.getInt("semester");
		f.stunden = result.getInt("stunden");
		return f;
	}
	
	/**
	 * Constructs a FachInstanz
	 * @param id the id
	 * @throws WebServiceException
	 */
	public FachInstanz(int id) throws WebServiceException {
		if(id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		this.id = id;
	}
	
	/**
	 * Reads all attributes of a FachInstanz from the database
	 * @return the FachInstanz with all attributes filled
	 * @throws WebServiceException
	 */
	public FachInstanz getDirectAttributes() throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(id);
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT fach, modulInstanz, semester, stunden FROM fachinstanz WHERE id = ?", fieldValues);
		if(resultList.isEmpty()) {
			throw new WebServiceException(ExceptionStatus.OBJECT_NOT_FOUND);
		}
		TypeHashMap<String, Object> result = resultList.get(0);
		fach = new Fach(result.getInt("fach")).getDirectAttributes();
		modulInstanzID = result.getInt("modulInstanz");
		semester = result.getInt("semester");
		stunden = result.getInt("stunden");
		return this;
	}
	
	/**
	 * Creates a FachInstanz
	 * @return the created FachInstanz
	 * @throws WebServiceException
	 */
	public FachInstanz create() throws WebServiceException {
		/*if (id != 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}*/
		if(fach.getID() == 0) {
			fach.create();
		}
		checkDirectAttributes();		
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(fach.getID());
		fieldValues.add(modulInstanzID);
		fieldValues.add(semester);
		fieldValues.add(stunden);
		fieldValues.add(semester);
		fieldValues.add(stunden);
		int newID = db.doQuery("INSERT INTO fachinstanz (fach, modulInstanz, semester, stunden) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE semester = ?, stunden = ?", fieldValues);
		if(id <= 0) {
			id = newID;
		}
		return this;
	}
	
	/**
	 * Deletes a FachInstanz
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
		db.doQuery("DELETE FROM fachinstanz WHERE id = ?", fieldValues);
		//Delete Fach if there are no Kompetenzfach connections and no FachInstanzen for this Fach
		if (fach.getInstanzenCount() == 0 && Dozent.getAllForFach(fach).isEmpty()) {
			fach.delete();	
		}
	}
	
	/**
	 * Checks all attributes of a FachInstanz
	 * @throws WebServiceException if an attribute is invalid
	 */
	private void checkDirectAttributes() throws WebServiceException {
		if(fach == null || fach.getID() <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_OBJECT);
		} else {
			fach.getDirectAttributes(); //check existance
		}
		if (modulInstanzID <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		} else {
			new ModulInstanz(modulInstanzID).getDirectAttributes(); //check existance
		}
		if (semester < 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_NUMBER);
		}
		if (stunden < 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_NUMBER);
		}
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public void setModulInstanzID(int modulInstanzID) {
		this.modulInstanzID = modulInstanzID;
	}
	
	public int getID() {
		return id;
	}
	
	public int getStunden() {
		return stunden;
	}
	
	/**
	 * It is only allowed to call this method, when fach.setID() follows this call or {@link #getDirectAttributes()} was called before
	 */
	public Fach getFach() {
		if(fach == null) {
			try {
				return new Fach(1);
			} catch (WebServiceException e) {
				return null;
			}
		} else {
			return fach;
		}
	}
	
}
