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

/**
 * A class to represent the GroupE CSV export
 */
public class GroupE {

	/**
	 * Generates a CSV file to use for GroupE containing all data about Vorlesungen of a Kurs in a given Semester
	 * @param kurs the kurs
	 * @param semester the semester
	 * @return the CSV file
	 * @throws WebServiceException
	 */
	public static File getCSVData(Kurs kurs, int semester) throws WebServiceException {
		if(kurs == null) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_ID);
		}
		kurs.getDirectAttributes(); //check existance
		if(semester <= 0) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_NUMBER);
		}
		//Constant Values for GroupE-Export
		String ConstNoInvites = "1";
		String ConstVisibility = "@all";
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(kurs.getID());
		fieldValues.add(semester);
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("Select t.datum as 'StartDate', t.startUhrzeit as 'StartTime', t.endUhrzeit as 'EndTime', f.name as 'Subject', t.raum as 'Resources', k.kursName as 'Owner', d.Name as 'Attendees', 'Vorlesung' as 'Category' from vvs.termin as t join vvs.vorlesung as v on t.Vorlesung = v.ID join vvs.kurs as k on v.kurs = k.ID join vvs.dozent as d on v.Dozent = d.ID join vvs.fachinstanz as fi on v.fachInstanz = fi.ID join vvs.fach as f on fi.fach = f.ID where v.Kurs = ? and v.Semester = ?",fieldValues);
		File file = new File("export.csv");
		PrintWriter out;
		try {
			out = new PrintWriter(file);	
		} catch (FileNotFoundException e) {
			throw new WebServiceException(ExceptionStatus.FILE_CREATION_ERROR);
		}
		out.println("StartDate;StartTime;EndTime;Subject;Resources;Owner;Attendees;Category;NoInvites;Visibility");
		for(TypeHashMap<String, Object> result : resultList) {
			out.println(result.get("StartDate").toString()+";"+result.get("StartTime").toString()+";"+result.get("EndTime").toString()+";"+result.get("Subject").toString()+";"+result.get("Resources").toString()+";"+result.get("Owner").toString()+";"+result.get("Attendees").toString()+";"+result.get("Category").toString()+";"+ConstNoInvites+";"+ConstVisibility);
		}
		out.close();
		return file;
	}
		
}
