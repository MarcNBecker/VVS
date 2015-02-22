package de.dhbw.vvs.model;

import java.sql.Date;
import java.util.ArrayList;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.database.ConnectionPool;
import de.dhbw.vvs.database.DatabaseConnection;
import de.dhbw.vvs.utility.TypeHashMap;
import de.dhbw.vvs.utility.Utility;

public class Feiertag {

	private Date datum;
	private String name;
	
	public static ArrayList<Feiertag> getAll(int jahr) throws WebServiceException {
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(jahr);
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT datum, name FROM feiertag WHERE YEAR(datum) = ? ORDER BY datum ASC", fieldValues);
		ArrayList<Feiertag> feiertagList = new ArrayList<Feiertag>();
		for(TypeHashMap<String, Object> result : resultList) {
			Feiertag f = new Feiertag((Date) result.get("datum"));
			f.name = result.getString("name");
			feiertagList.add(f);
		}
		return feiertagList;
	}
	
	public Feiertag(Date datum) throws WebServiceException {
		if(datum == null) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_DATE);
		}
		this.datum = datum;
	}
	
	public Feiertag update() throws WebServiceException {
		checkDirectAttributes();
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(Utility.dateString(datum));
		fieldValues.add(name);
		fieldValues.add(name);
		db.doQuery("INSERT INTO feiertag (datum, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE name = ?", fieldValues);
		return this;
	}
	
	public void delete() throws WebServiceException {
		if (datum == null) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_DATE);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(Utility.dateString(datum));
		db.doQuery("DELETE FROM feiertag WHERE datum = ?", fieldValues);
	}
	
	private void checkDirectAttributes() throws WebServiceException {
		if (datum == null) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_DATE);
		}
		if (name == null || (name = name.trim()).isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
	}
	
	public void setDatum(Date datum) {
		this.datum = datum;
	}
	
}
