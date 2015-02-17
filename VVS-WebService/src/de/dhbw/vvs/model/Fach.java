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
	
	public static ArrayList<Fach> getAllForDozent(Dozent dozent) throws WebServiceException {
		if (dozent.getID() <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(dozent.getID());
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT fach.id, fach.name, fach.kurzbeschreibung FROM dozentfach INNER JOIN fach ON dozentfach.fach = fach.id WHERE dozentfach.dozent = ?", fieldValues);
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
		//TODO implement this when implementing modulplan
		name = "Testfach";
		kurzbeschreibung = "Dies ist ein Testfach";
		return this;
	}
	
	public int getID() {
		return id;
	}
	
}
