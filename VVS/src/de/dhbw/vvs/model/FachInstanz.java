package de.dhbw.vvs.model;

import java.util.ArrayList;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.database.ConnectionPool;
import de.dhbw.vvs.database.DatabaseConnection;
import de.dhbw.vvs.utility.TypeHashMap;

public class FachInstanz {

	private int id;
	private Fach fach;
	private int modulInstanzID;
	private int semester;
	private int stunden;
	
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
	
	public static ArrayList<FachInstanz> getAllMissingForKurs(Kurs kurs) throws WebServiceException {
		if (kurs == null) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT);
		}
		kurs.getDirectAttributes(); //check existance
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(kurs.getID());
		fieldValues.add(kurs.getModulplanID());
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT id, fach, modulInstanz, semester, stunden FROM fachinstanz WHERE id NOT IN (SELECT fachInstanz FROM vorlesung WHERE kurs = ?) AND modulInstanz IN (SELECT id FROM modulInstanz WHERE modulplan = ?)", fieldValues);
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
	
	public FachInstanz(int id) throws WebServiceException {
		if(id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		this.id = id;
	}
	
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
	
	public FachInstanz create() throws WebServiceException {
		if (id != 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
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
		this.id = db.doQuery("INSERT INTO fachinstanz (fach, modulInstanz, semester, stunden) VALUES (?, ?, ?, ?)", fieldValues);
		return this;
	}
	
	public void delete() throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		getDirectAttributes();
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(id);
		db.doQuery("DELETE FROM fachinstanz WHERE id = ?", fieldValues);
		if (fach.getInstanzenCount() == 0) {
			fach.delete();	
		}
	}
	
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
		if (semester <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_NUMBER);
		}
		if (stunden < 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_NUMBER);
		}
	}
	
	public void setModulInstanzID(int modulInstanzID) {
		this.modulInstanzID = modulInstanzID;
	}
	
	public int getID() {
		return id;
	}
	
	/**
	 * It is only allowed to call this method, when fach.setID() follows this call
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
