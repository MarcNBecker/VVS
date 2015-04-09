package de.dhbw.vvs.model;

import java.sql.Timestamp;
import java.util.ArrayList;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.database.ConnectionPool;
import de.dhbw.vvs.database.DatabaseConnection;
import de.dhbw.vvs.utility.TypeHashMap;
import de.dhbw.vvs.utility.Utility;

/**
 * Class to represent a Dozent
 */
public class Dozent {

	private int id;
	
	//Direct Attributes
	private String titel;
	private String name;
	private String vorname;
	private Geschlecht geschlecht;
	private String strasse;
	private String wohnort;
	private String postleitzahl;
	private String mail;
	private String telefonPrivat;
	private String telefonMobil;
	private String telefonGeschaeftlich;
	private String fax;
	private String arbeitgeber;
	private Status status;
	@SuppressWarnings("unused")
	private Timestamp angelegt;
	@SuppressWarnings("unused")
	private Timestamp geaendert;
	@SuppressWarnings("unused")
	private int maxFachJahr; //This is only filled at times, when the dozent is loaded in context with a fach information
	
	/**
	 * Returns a list of all Dozenten with minimized information
	 * @return a list of all Dozenten
	 * @throws WebServiceException
	 */
	public static ArrayList<Dozent> getAll() throws WebServiceException {
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT id, titel, name, vorname, status FROM dozent ORDER BY name ASC", null);
		ArrayList<Dozent> dozentList = new ArrayList<Dozent>();
		for(TypeHashMap<String, Object> result : resultList) {
			Dozent d = new Dozent(result.getInt("id"));
			d.titel = result.getString("titel");
			d.name = result.getString("name");
			d.vorname = result.getString("vorname");
			d.status = Status.getFromOrdinal(result.getInt("status"));		
			dozentList.add(d);
		}
		return dozentList;
	}
	
	/**
	 * Returns a list of Dozenten that can teach a specific Fach
	 * @param fach the fach
	 * @return a list of Dozenten
	 * @throws WebServiceException
	 */
	public static ArrayList<Dozent> getAllForFach(Fach fach) throws WebServiceException {
		if(fach == null || fach.getID() <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(fach.getID());
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT id, titel, name, vorname, status FROM dozent INNER join dozentfach ON dozent.id = dozentfach.dozent WHERE fach = ? ORDER BY name ASC", fieldValues);
		ArrayList<Dozent> dozentList = new ArrayList<Dozent>();
		for(TypeHashMap<String, Object> result : resultList) {
			Dozent d = new Dozent(result.getInt("id"));
			d.titel = result.getString("titel");
			d.name = result.getString("name");
			d.vorname = result.getString("vorname");
			d.status = Status.getFromOrdinal(result.getInt("status"));
			dozentList.add(d);
		}
		return dozentList;
	}
	
	/**
	 * Returns a list of Dozenten that the Studiengangsleiter of a Kurs has previously used for a specific Fach
	 * @param vorlesung the vorlesung of a Kurs for a specific Fach
	 * @return a list of Dozenten
	 * @throws WebServiceException
	 */
	public static ArrayList<Dozent> getAllForVorlesung(Vorlesung vorlesung) throws WebServiceException {
		if(vorlesung == null || vorlesung.getID() <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		vorlesung.getDirectAttributes();
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(vorlesung.getKursID());
		fieldValues.add(vorlesung.getFachInstanz().getFach().getID());
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT id, titel, name, vorname, status FROM dozent WHERE id IN (SELECT dozent FROM vorlesung WHERE kurs IN (SELECT id FROM kurs WHERE studiengangsleiter = (SELECT studiengangsleiter FROM kurs WHERE id = ?)) AND fachInstanz IN (SELECT id FROM fachInstanz WHERE fach = ?)) ORDER BY name ASC", fieldValues);
		ArrayList<Dozent> dozentList = new ArrayList<Dozent>();
		for(TypeHashMap<String, Object> result : resultList) {
			Dozent d = new Dozent(result.getInt("id"));
			d.titel = result.getString("titel");
			d.name = result.getString("name");
			d.vorname = result.getString("vorname");
			d.status = Status.getFromOrdinal(result.getInt("status"));
			dozentList.add(d);
		}
		return dozentList;
	}
	
	/**
	 * Returns a list of Dozenten that teach one ore more Faecher for a Kurs in a Semester
	 * @param kurs the kurs
	 * @param semester the semester
	 * @return a list of Dozenten
	 * @throws WebServiceException
	 */
	public static ArrayList<Dozent> getAllForKursSemester(Kurs kurs, int semester) throws WebServiceException {
		if(kurs == null || kurs.getID() <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		if(semester <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_NUMBER);
		}
		kurs.getDirectAttributes(); //check existance
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(kurs.getID());
		fieldValues.add(semester);
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT id, titel, name, vorname, status FROM dozent WHERE id IN (SELECT dozent FROM vorlesung WHERE kurs = ? AND semester = ?) ORDER BY name ASC", fieldValues);
		ArrayList<Dozent> dozentList = new ArrayList<Dozent>();
		for(TypeHashMap<String, Object> result : resultList) {
			Dozent d = new Dozent(result.getInt("id"));
			d.titel = result.getString("titel");
			d.name = result.getString("name");
			d.vorname = result.getString("vorname");
			d.status = Status.getFromOrdinal(result.getInt("status"));
			dozentList.add(d);
		}
		return dozentList;
	}
	
	/**
	 * Creates a Dozent by an ID
	 * @param id the ID
	 * @throws WebServiceException
	 */
	public Dozent(int id) throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		this.id = id;
	}
	
	/**
	 * Loads all attributes of a Dozent
	 * @return the Dozent with all attributes loaded
	 * @throws WebServiceException
	 */
	public Dozent getDirectAttributes() throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(id);
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT titel, name, vorname, geschlecht, strasse, wohnort, postleitzahl, mail, telefonPrivat, telefonMobil, telefonGeschaeftlich, fax, arbeitgeber, status, angelegt, geaendert FROM dozent WHERE id = ?", fieldValues);
		if(resultList.isEmpty()) {
			throw new WebServiceException(ExceptionStatus.OBJECT_NOT_FOUND);
		}
		TypeHashMap<String, Object> result = resultList.get(0);
		titel = result.getString("titel");
		name = result.getString("name");
		vorname = result.getString("vorname");
		geschlecht = Geschlecht.getFromOrdinal(result.getInt("geschlecht"));
		strasse = result.getString("strasse");
		wohnort = result.getString("wohnort");
		postleitzahl = result.getString("postleitzahl");
		mail = result.getString("mail");
		telefonPrivat = result.getString("telefonPrivat");
		telefonMobil = result.getString("telefonMobil");
		telefonGeschaeftlich = result.getString("telefonGeschaeftlich");
		fax = result.getString("fax");
		arbeitgeber = result.getString("arbeitgeber");
		status = Status.getFromOrdinal(result.getInt("status"));
		angelegt = (Timestamp) result.get("angelegt");
		geaendert = (Timestamp) result.get("geaendert");
		return this;
	}
	
	/**
	 * Fills the lastHeld attribute of a Dozent, wich tells in which year a Dozent last held a specific Fach
	 * @param fach the Fach
	 * @param setOnFach specifies if the lastHeld attribute should be set on the Fach rather than on the Dozent
	 * @throws WebServiceException
	 */
	public void lastHeld(Fach fach, boolean setOnFach) throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		if(fach == null) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_OBJECT);
		} else {
			fach.getDirectAttributes(); //check existance
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(id);
		fieldValues.add(fach.getID());
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT MAX(YEAR(termin.datum)) AS maxJahr FROM termin INNER JOIN vorlesung ON termin.vorlesung = vorlesung.id WHERE vorlesung.dozent = ? AND vorlesung.fachInstanz IN (SELECT id FROM fachInstanz WHERE fach = ?) GROUP BY vorlesung.dozent", fieldValues);
		if(resultList.isEmpty()) {
			if(setOnFach) {
				fach.setMaxJahr(0);
			} else {
				maxFachJahr = 0;	
			}
		} else {
			if(setOnFach) {
				fach.setMaxJahr(resultList.get(0).getInt("maxJahr"));
			} else {
				maxFachJahr = resultList.get(0).getInt("maxJahr");	
			}	
		}	
	}
	
	/**
	 * Returns a list of all Kompetenzfaecher of a Dozent
	 * @return a list of all Kompetenzfaecher
	 * @throws WebServiceException
	 */
	public ArrayList<Fach> getFachList() throws WebServiceException {
		return Fach.getAllForDozent(this);
	}
	
	/**
	 * Returns a list of all Kommentare for a Dozent
	 * @return a list of all Kommentare
	 * @throws WebServiceException
	 */
	public ArrayList<Kommentar> getKommentarList() throws WebServiceException {
		return Kommentar.getAll(this);
	}
	
	/**
	 * Creates a Dozent
	 * @return the created Dozent
	 * @throws WebServiceException
	 */
	public Dozent create() throws WebServiceException {
		if (id != 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		checkDirectAttributes();
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(titel);
		fieldValues.add(name);
		fieldValues.add(vorname);
		fieldValues.add(geschlecht);
		fieldValues.add(strasse);
		fieldValues.add(wohnort);
		fieldValues.add(postleitzahl);
		fieldValues.add(mail);
		fieldValues.add(telefonPrivat);
		fieldValues.add(telefonMobil);
		fieldValues.add(telefonGeschaeftlich);
		fieldValues.add(fax);
		fieldValues.add(arbeitgeber);
		fieldValues.add(status);
		this.id = db.doQuery("INSERT INTO dozent (titel, name, vorname, geschlecht, strasse, wohnort, postleitzahl, mail, telefonPrivat, telefonMobil, telefonGeschaeftlich, fax, arbeitgeber, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", fieldValues);
		return this.getDirectAttributes(); //Get Timestamps
	}
	
	/**
	 * Updates a Dozent
	 * @return the updated Dozent
	 * @throws WebServiceException
	 */
	public Dozent update() throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		checkDirectAttributes();
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(titel);
		fieldValues.add(name);
		fieldValues.add(vorname);
		fieldValues.add(geschlecht);
		fieldValues.add(strasse);
		fieldValues.add(wohnort);
		fieldValues.add(postleitzahl);
		fieldValues.add(mail);
		fieldValues.add(telefonPrivat);
		fieldValues.add(telefonMobil);
		fieldValues.add(telefonGeschaeftlich);
		fieldValues.add(fax);
		fieldValues.add(arbeitgeber);
		fieldValues.add(status);
		fieldValues.add(id);
		int affectedRows = db.doQuery("UPDATE dozent SET titel = ?, name = ?, vorname = ?, geschlecht = ?, strasse = ?, wohnort = ?, postleitzahl = ?, mail = ?, telefonPrivat = ?, telefonMobil = ?, telefonGeschaeftlich = ?, fax = ?, arbeitgeber = ?, status = ? WHERE id = ?", fieldValues);
		if(affectedRows == 0) {
			throw new WebServiceException(ExceptionStatus.OBJECT_NOT_FOUND);
		} else {
			return this.getDirectAttributes(); //Get Timestamps
		}
	}
	
	/**
	 * Adds a Fach to the Dozent as a Kompetenzfach
	 * @param fach the fach
	 * @return the fach
	 * @throws WebServiceException
	 */
	public Fach addFach(Fach fach) throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		if (fach.getID() <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);	
		}
		fach.getDirectAttributes(); //check existance
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(id);
		fieldValues.add(fach.getID());
		db.doQuery("INSERT INTO dozentfach (dozent, fach) VALUES (?, ?) ON DUPLICATE KEY UPDATE dozent = dozent, fach = fach", fieldValues);
		return fach;
	}
	
	/**
	 * Adds a Kommentar to the Dozent
	 * @param kommentar the Kommentar
	 * @return the added Kommentar
	 * @throws WebServiceException
	 */
	public Kommentar addKommentar(Kommentar kommentar) throws WebServiceException {
		kommentar.setDozentID(id);
		return kommentar.create();
	}
	
	/**
	 * Deletes a Dozent
	 * @throws WebServiceException
	 */
	public void delete() throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(id);
		db.doQuery("DELETE FROM dozent WHERE id = ?", fieldValues);
	}
	
	/**
	 * Deletes a Kompetenzfach of a Dozent
	 * @param fach the Fach
	 * @throws WebServiceException
	 */
	public void deleteFach(Fach fach) throws WebServiceException {
		if (id <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		if (fach.getID() <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);	
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(id);
		fieldValues.add(fach.getID());
		db.doQuery("DELETE FROM dozentfach WHERE dozent = ? AND fach = ?", fieldValues);
		if (fach.getInstanzenCount() == 0 && Dozent.getAllForFach(fach).isEmpty()) {
			fach.delete();	
		}
	}
	
	/**
	 * Deletes a Kommentar for a Dozent
	 * @param kommentar the Kommentar
	 * @throws WebServiceException
	 */
	public void deleteKommentar(Kommentar kommentar) throws WebServiceException {
		kommentar.setDozentID(id);
		kommentar.delete();
	}
	
	/**
	 * Checks all attributes of a Dozent
	 * @throws WebServiceException if an attribute is invalid
	 */
	private void checkDirectAttributes() throws WebServiceException {
		if (titel == null) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
		if (name == null || (name = name.trim()).isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
		if (vorname == null || (vorname = vorname.trim()).isEmpty()) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
		if (geschlecht == null) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ENUM);
		}
		if (strasse == null) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
		if (wohnort == null) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
		if (postleitzahl == null || (!(postleitzahl = postleitzahl.trim()).isEmpty() && !Utility.checkNumeric(postleitzahl))) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_NUMBER);
		}
		if (mail == null || (!(mail = mail.trim()).isEmpty() && !Utility.checkEmail(mail))) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_MAIL);
		}
		if (telefonPrivat == null || (!(telefonPrivat = telefonPrivat.trim()).isEmpty() && !Utility.checkPhone(telefonPrivat))) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_PHONE);
		}
		if (telefonMobil == null || (!(telefonMobil = telefonMobil.trim()).isEmpty() && !Utility.checkPhone(telefonMobil))) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_PHONE);
		}
		if (telefonGeschaeftlich == null || (!(telefonGeschaeftlich = telefonGeschaeftlich.trim()).isEmpty() && !Utility.checkPhone(telefonGeschaeftlich))) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_PHONE);
		}
		if (fax == null || (!(fax = fax.trim()).isEmpty() && !Utility.checkPhone(fax))) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_PHONE);
		}
		if (arbeitgeber == null) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_STRING);
		}
		if (status == null) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ENUM);
		}
	}
	
	public int getID() {
		return id;
	}
	
	public void setID(int id) throws WebServiceException {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getVorname() {
		return vorname;
	}
}
