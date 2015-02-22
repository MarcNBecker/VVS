package de.dhbw.vvs.model;

import java.util.ArrayList;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.database.ConnectionPool;
import de.dhbw.vvs.database.DatabaseConnection;
import de.dhbw.vvs.utility.TypeHashMap;

public class Fach {

	private int id;
	private String name; 
	private String kurzbeschreibung;
	
	public static ArrayList<Fach> getAll() throws WebServiceException {
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT id, name, kurzbeschreibung FROM fach ORDER BY name ASC", null);
		ArrayList<Fach> fachList = new ArrayList<Fach>();
		for(TypeHashMap<String, Object> result : resultList) {
			Fach f = new Fach(result.getInt("id"));
			f.name = result.getString("name");
			f.kurzbeschreibung = result.getString("kurzbeschreibung");
			fachList.add(f);
		}
		return fachList;
	}
	
	public static ArrayList<Fach> getAllForDozent(Dozent dozent) throws WebServiceException {
		if (dozent.getID() <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(dozent.getID());
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT fach.id, fach.name, fach.kurzbeschreibung FROM dozentfach INNER JOIN fach ON dozentfach.fach = fach.id WHERE dozentfach.dozent = ? ORDER BY fach.name ASC", fieldValues);
		ArrayList<Fach> fachList = new ArrayList<Fach>();
		for(TypeHashMap<String, Object> result : resultList) {
			Fach f = new Fach(result.getInt("id"));
			f.name = result.getString("name");
			f.kurzbeschreibung = result.getString("kurzbeschreibung");
			fachList.add(f);
		}
		return fachList;
	}
	
	public Fach(int id) throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		this.id = id;
	}
	
	public Fach getDirectAttributes() throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(id);
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT name, kurzbeschreibung FROM fach WHERE id = ?", fieldValues);
		if(resultList.isEmpty()) {
			throw new WebServiceException(ExceptionStatus.OBJECT_NOT_FOUND);
		}
		TypeHashMap<String, Object> result = resultList.get(0);
		name = result.getString("name");
		kurzbeschreibung = result.getString("kurzbeschreibung");
		return this;
	}
	
	public Fach create() throws WebServiceException {
		if (id != 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		checkDirectAttributes();
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(name);
		fieldValues.add(kurzbeschreibung);
		this.id = db.doQuery("INSERT INTO fach (name, kurzbeschreibung) VALUES (?, ?)", fieldValues);
		return this;
	}
	
	public void delete() throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(id);
		db.doQuery("DELETE FROM fach WHERE id = ?", fieldValues);
	}
	
	private void checkDirectAttributes() throws WebServiceException {
		if (name == null || (name = name.trim()).isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
		if (kurzbeschreibung == null || (kurzbeschreibung = kurzbeschreibung.trim()).isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
	}
	
	int getInstanzenCount() throws WebServiceException {
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(id);
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT COUNT(*) AS c FROM fachinstanz WHERE fach = ?", fieldValues);
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
