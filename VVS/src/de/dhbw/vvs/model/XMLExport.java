package de.dhbw.vvs.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.database.ConnectionPool;
import de.dhbw.vvs.database.DatabaseConnection;
import de.dhbw.vvs.utility.TypeHashMap;

public class XMLExport {

	public static File getXMLData(Kurs kurs, int semester) throws WebServiceException {
		if(kurs == null) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		kurs.getDirectAttributes(); //check existance
		if(semester <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_NUMBER);
		}
		File file = new File("export.xml");
		PrintWriter out;
		try {
			out = new PrintWriter(file);	
		} catch (FileNotFoundException e) {
			throw new WebServiceException(ExceptionStatus.FILE_CREATION_ERROR);
		}
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(kurs.getID());
		fieldValues.add(semester);
		//TODO GET DATA FOR XML-EXPORT
		//Get general data
		ArrayList<TypeHashMap<String, Object>> resultListGeneral = db.doSelectingQuery("Select k.kursName, k.StudentenAnzahl, s.Name, k.SekretariatName, m.Studiengang, m.Vertiefungsrichtung, b.StartDatum, b.EndDatum, b.Raum from kurs as k join vvs.modulplan as m on k.Modulplan = m.ID join vvs.blocklage as b on k.ID = b.Kurs join vvs.studiengangsleiter as s on s.ID = k.Studiengangsleiter where b.Semester = ? and k.ID = ?;",fieldValues);
				
		//Get dates
		ArrayList<TypeHashMap<String, Object>> resultListDates = db.doSelectingQuery("Select t.datum as 'StartDate', t.startUhrzeit as 'StartTime', t.endUhrzeit as 'EndTime', f.name as 'Subject', t.raum, k.kursName, d.Name as 'Dozent' from vvs.termin as t join vvs.vorlesung as v on t.Vorlesung = v.ID join vvs.kurs as k on v.kurs = k.ID join vvs.dozent as d on v.Dozent = d.ID join vvs.fachinstanz as fi on v.fachInstanz = fi.ID join vvs.fach as f on fi.fach = f.ID where v.Semester = ? and k.ID = ?;",fieldValues);
		
		//Get holidays //TODO Blocklage als Parameter?
		ArrayList<Object> blocklage = new ArrayList<Object>();
		//blocklage.add(); //Startdatum Blocklage
		//blocklage.add(); //Enddatum Blocklage
		ArrayList<TypeHashMap<String, Object>> resultListHolidays = db.doSelectingQuery("SELECT * FROM `feiertag` WHERE Datum >= ? AND Datum <= ?",blocklage);
		
		//TODO DO XML STUFF HERE
		out.close();
		return file;
	}
	
}
