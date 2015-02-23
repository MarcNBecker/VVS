package de.dhbw.vvs.model;

import java.sql.Timestamp;
import java.util.ArrayList;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.database.ConnectionPool;
import de.dhbw.vvs.database.DatabaseConnection;
import de.dhbw.vvs.utility.TypeHashMap;

public class Kommentar {
	
	private int id;
	private int dozentID;
	private String text;
	private String verfasser;
	@SuppressWarnings("unused")
	private Timestamp timestamp;
	
	public static ArrayList<Kommentar> getAll(Dozent dozent) throws WebServiceException {
		if (dozent.getID() <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(dozent.getID());
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT id, dozent, text, verfasser, timestamp FROM kommentar WHERE dozent = ? ORDER BY timestamp DESC", fieldValues);
		ArrayList<Kommentar> kommentarList = new ArrayList<Kommentar>();
		for(TypeHashMap<String, Object> result : resultList) {
			Kommentar k = new Kommentar(result.getInt("id"));
			k.dozentID = result.getInt("dozent");
			k.text = result.getString("text");
			//verfasser can become null, when the user is deleted who posted the Kommentar
			Object verfasser = result.get("verfasser");
			if(verfasser != null) {
				k.verfasser = (String) verfasser;	
			} else {
				k.verfasser = "";
			}
			k.timestamp = (Timestamp) result.get("timestamp");
			kommentarList.add(k);
		}
		return kommentarList;
	}
	
	public Kommentar(int id) throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		this.id = id;
	}
	
	Kommentar create() throws WebServiceException {
		if (id != 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		checkDirectAttributes();
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(dozentID);
		fieldValues.add(text);
		fieldValues.add(verfasser);
		this.id = db.doQuery("INSERT INTO kommentar (dozent, text, verfasser) VALUES (?, ?, ?)", fieldValues);
		this.timestamp = new Timestamp(System.currentTimeMillis());
		return this;
	}
	
	void delete() throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		if (dozentID <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID); //needed to check if kommentar actually belongs to dozent	
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(id);
		fieldValues.add(dozentID);
		db.doQuery("DELETE FROM kommentar WHERE id = ? AND dozent = ?", fieldValues);
	}
	
	private void checkDirectAttributes() throws WebServiceException {
		if (dozentID <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		} else {
			new Dozent(dozentID).getDirectAttributes(); //check existance
		}
		if (text == null || (text = text.trim()).isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
		if (verfasser == null || (verfasser = verfasser.trim()).isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		} else {
			new User(verfasser).checkExistance(); //check existance
		}
	}
	
	public int getDozentID() {
		return this.dozentID;
	}
	
	public void setDozentID(int dozentID) {
		this.dozentID = dozentID;
	}
	
}
