package de.dhbw.vvs.model;

import java.util.ArrayList;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.database.ConnectionPool;
import de.dhbw.vvs.database.DatabaseConnection;
import de.dhbw.vvs.utility.TypeHashMap;

public class Vorlesung {

	private int id;
	private int kursID;
	private FachInstanz fachInstanz;
	private int dozentID;
	private int semester;
	
	public static ArrayList<Vorlesung> getAll(Kurs kurs, int semester) throws WebServiceException {
		if (kurs == null) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT);
		}
		kurs.getDirectAttributes(); //check existance
		if (semester <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_NUMBER);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(kurs.getID());
		fieldValues.add(semester);
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT id, kurs, fachInstanz, dozent, semester FROM vorlesung WHERE kurs = ? AND semester = ? ORDER BY fachInstanz ASC", fieldValues);
		ArrayList<Vorlesung> vorlesungList = new ArrayList<Vorlesung>();
		for(TypeHashMap<String, Object> result : resultList) {
			Vorlesung v = new Vorlesung(result.getInt("id"));
			v.kursID = result.getInt("kurs");
			v.fachInstanz = new FachInstanz(result.getInt("fachInstanz")).getDirectAttributes();
			v.dozentID = result.getInt("dozent");
			v.semester = result.getInt("semester");
			vorlesungList.add(v);
		}
		return vorlesungList;
	}
	
	public static ArrayList<Vorlesung> getAllForKurs(Kurs kurs) throws WebServiceException {
		if (kurs == null) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT);
		}
		kurs.getDirectAttributes(); //check existance
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(kurs.getID());
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT id, kurs, fachInstanz, dozent, semester FROM vorlesung WHERE kurs = ? ORDER BY semester ASC", fieldValues);
		ArrayList<Vorlesung> vorlesungList = new ArrayList<Vorlesung>();
		for(TypeHashMap<String, Object> result : resultList) {
			Vorlesung v = new Vorlesung(result.getInt("id"));
			v.kursID = result.getInt("kurs");
			v.fachInstanz = new FachInstanz(result.getInt("fachInstanz")).getDirectAttributes();
			v.dozentID = result.getInt("dozent");
			v.semester = result.getInt("semester");
			vorlesungList.add(v);
		}
		return vorlesungList;
	}
	
	public Vorlesung(int id) throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		this.id = id;
	}
	
	public Vorlesung getDirectAttributes() throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(id);
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT kurs, fachInstanz, dozent, semester FROM vorlesung WHERE id = ?", fieldValues);
		if(resultList.isEmpty()) {
			throw new WebServiceException(ExceptionStatus.OBJECT_NOT_FOUND);
		}
		TypeHashMap<String, Object> result = resultList.get(0);
		kursID = result.getInt("kurs");
		fachInstanz = new FachInstanz(result.getInt("fachInstanz")).getDirectAttributes();
		dozentID = result.getInt("dozent");
		semester = result.getInt("semester");
		return this;
	}
	
	public Vorlesung create() throws WebServiceException {
		if (id != 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		checkDirectAttributes();
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(kursID);
		fieldValues.add(fachInstanz.getID());
		fieldValues.add(dozentID == 0 ? null: 0);
		fieldValues.add(semester);
		this.id = db.doQuery("INSERT INTO vorlesung (kurs, fachInstanz, dozent, semester) VALUES (?, ?, ?, ?)", fieldValues);
		return this;
	}
	
	public Vorlesung update() throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		checkDirectAttributes();
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(kursID);
		fieldValues.add(fachInstanz.getID());
		fieldValues.add(dozentID == 0 ? null: 0);
		fieldValues.add(semester);
		fieldValues.add(id);
		int affectedRows = db.doQuery("UPDATE vorlesung SET kurs = ?, fachInstanz = ?, dozent = ?, semester = ? WHERE id = ?", fieldValues);
		if(affectedRows == 0) {
			throw new WebServiceException(ExceptionStatus.OBJECT_NOT_FOUND);
		} else {
			return this;
		}
	}
	
	public void delete() throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(id);
		db.doQuery("DELETE FROM vorlesung WHERE id = ?", fieldValues);
	}
	
	public void checkDirectAttributes() throws WebServiceException {
		if (kursID <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		} else {
			new Kurs(kursID).getDirectAttributes(); //check existance
		}
		if (fachInstanz.getID() <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		} else {
			fachInstanz.getDirectAttributes(); //check existance
		}
		//allow dozent ID 0
		if (dozentID < 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		} else if (dozentID != 0) {
			new Dozent(dozentID).getDirectAttributes(); //check existance
		}
		if (semester <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_NUMBER);
		}
	}
	
	public int getID() {
		return id;
	}
	
	public int getKursID() {
		return kursID;
	}
	
	public FachInstanz getFachInstanz() {
		return fachInstanz;
	}
	
	public void setKursID(int kursID) {
		this.kursID = kursID;
	}
	
	public void setSemester(int semester) {
		this.semester = semester;
	}
	
}
