package de.dhbw.vvs.model;

import java.util.ArrayList;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.database.ConnectionPool;
import de.dhbw.vvs.database.DatabaseConnection;
import de.dhbw.vvs.utility.TypeHashMap;

public class Modulplan {

	private int id;
	private String studiengang;
	private String vertiefungsrichtung;
	private int gueltigAb;
	
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
	
	public Modulplan(int id) throws WebServiceException {
		if(id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		this.id = id;
	}
	
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
	
	public ArrayList<ModulInstanz> getModulList() throws WebServiceException {
		return ModulInstanz.getAll(this);
	}
	
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
	
	public void delete() throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(id);
		db.doQuery("DELETE FROM modulplan WHERE id = ?", fieldValues);
	}
	
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
