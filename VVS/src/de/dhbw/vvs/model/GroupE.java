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

@SuppressWarnings("unused")
public class GroupE {
	
	private String StartDate;
	private String StartTime;
	private String EndTime;
	private String Subject;
	private String Resources;
	private String Owner;
	private String Attendees; 
	private String NoInvites; 
	private String Category;
	private String Visibility;
	
	public GroupE() {
		
	}

	public static File getCSVData(int kursID, int semester) throws WebServiceException {
		//Constant Values for GroupE-Export
		String ConstNoInvites = "1";
		String ConstVisibility = "@all";
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(kursID);
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
	
	public static ArrayList<GroupE> getAll() throws WebServiceException {
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("Select t.datum as 'StartDate', t.startUhrzeit as 'StartTime', t.endUhrzeit as 'EndTime', f.name as 'Subject', t.raum as 'Resources', k.kursName as 'Owner', d.Name as 'Attendees', 'Vorlesung' as 'Category', '1' as 'NoInvites', '@all' as 'Visibility' from vvs.termin as t join vvs.vorlesung as v on t.Vorlesung = v.ID join vvs.kurs as k on v.kurs = k.ID join vvs.dozent as d on v.Dozent = d.ID join vvs.fachinstanz as fi on v.fachInstanz = fi.ID join vvs.fach as f on fi.fach = f.ID",null);
		ArrayList<GroupE> terminList = new ArrayList<GroupE>();
		for(TypeHashMap<String, Object> result : resultList) {
			GroupE d = new GroupE();
			d.StartDate = result.get("StartDate").toString();
			d.StartTime = result.get("StartTime").toString();
			d.EndTime = result.get("EndTime").toString();
			d.Subject = result.get("Subject").toString();
			d.Resources = result.get("Resources").toString();
			d.Owner = result.get("Owner").toString();
			d.Attendees = result.get("Attendees").toString();
			d.Category = result.get("Category").toString();
			d.NoInvites = result.get("NoInvites").toString();
			d.Visibility = result.get("Visibility").toString();
			terminList.add(d);
		}
		return terminList;
	}	
	
}
