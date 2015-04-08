package de.dhbw.vvs.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
		fieldValues.add(semester);
		fieldValues.add(kurs.getID());
		
		//TODO GET DATA FOR XML-EXPORT
		try {	
		//Get general data
		ArrayList<TypeHashMap<String, Object>> resultListGeneral = db.doSelectingQuery("Select k.kursName, k.StudentenAnzahl, s.Name, k.SekretariatName, m.Studiengang, m.Vertiefungsrichtung, b.StartDatum, b.EndDatum, b.Raum from kurs as k join vvs.modulplan as m on k.Modulplan = m.ID join vvs.blocklage as b on k.ID = b.Kurs join vvs.studiengangsleiter as s on s.ID = k.Studiengangsleiter where b.Semester = ? and k.ID = ?;",fieldValues);
			
		//Get dates
		ArrayList<TypeHashMap<String, Object>> resultListDates = db.doSelectingQuery("Select t.datum as 'StartDate', t.startUhrzeit as 'StartTime', t.endUhrzeit as 'EndTime', f.name as 'Subject', t.raum, k.kursName, d.Name as 'Dozent' from vvs.termin as t join vvs.vorlesung as v on t.Vorlesung = v.ID join vvs.kurs as k on v.kurs = k.ID join vvs.dozent as d on v.Dozent = d.ID join vvs.fachinstanz as fi on v.fachInstanz = fi.ID join vvs.fach as f on fi.fach = f.ID where v.Semester = ? and k.ID = ?;",fieldValues);
		
		String zeitraumStartString =  resultListGeneral.get(0).get("StartDatum").toString();
		String zeitraumEndeString = resultListGeneral.get(0).get("EndDatum").toString();
		//Get holidays //TODO Blocklage als Parameter?
		ArrayList<Object> blocklage = new ArrayList<Object>();
		blocklage.add(zeitraumStartString); //Startdatum Blocklage
		blocklage.add(zeitraumEndeString); //Enddatum Blocklage
		ArrayList<TypeHashMap<String, Object>> resultListHolidays = db.doSelectingQuery("SELECT * FROM `feiertag` WHERE Datum >= ? AND Datum <= ?",blocklage);
		
		//TODO DO XML STUFF HERE
		// Header Daten vorbereiten
		DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder build = dFact.newDocumentBuilder();
        Document doc = build.newDocument();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        Element semesterplan = doc.createElement("Semesterplan");
        root.appendChild(semesterplan);
        
        Element kopfdaten = doc.createElement("Kopfdaten");
        semesterplan.appendChild(kopfdaten);
        TypeHashMap<String, Object> resultGeneral = resultListGeneral.get(0);
        
        Element studiengang = doc.createElement("studiengang");
        studiengang.appendChild(doc.createTextNode(resultGeneral.get("Studiengang").toString()));
        kopfdaten.appendChild(studiengang);
		Element semesterXML = doc.createElement("semester");
		semesterXML.appendChild(doc.createTextNode(semester + ". Semester"));
		kopfdaten.appendChild(semesterXML);
		
		Element studienjahrgang = doc.createElement("studienjahrgang");
        studienjahrgang.appendChild(doc.createTextNode(resultGeneral.get("kursName").toString()));
        kopfdaten.appendChild(studienjahrgang);
		
        Element studileiter = doc.createElement("studiengangsleiter");
		studileiter.appendChild(doc.createTextNode(resultGeneral.get("Name").toString()));
		kopfdaten.appendChild(studileiter);
		
		Element zeitRaumStart = doc.createElement("zeitraumStart");
		zeitRaumStart.appendChild(doc.createTextNode(zeitraumStartString));
		kopfdaten.appendChild(zeitRaumStart);
		
		Element zeitRaumEnde = doc.createElement("zeitraumEnde");
		zeitRaumEnde.appendChild(doc.createTextNode(zeitraumEndeString));
		kopfdaten.appendChild(zeitRaumEnde);
		
		Element anzStudi = doc.createElement("anzahlStudierende");
		anzStudi.appendChild(doc.createTextNode(resultGeneral.get("StudentenAnzahl").toString()));
		kopfdaten.appendChild(anzStudi);
		
		Element graum = doc.createElement("raum");
		graum.appendChild(doc.createTextNode(resultGeneral.get("Raum").toString()));
		kopfdaten.appendChild(graum);
		
		Element sekraeter = doc.createElement("sekretaer");
		sekraeter.appendChild(doc.createTextNode(resultGeneral.get("SekretariatName").toString()));
		kopfdaten.appendChild(sekraeter);
		
		Element termine = doc.createElement("termine");
		semesterplan.appendChild(termine);
				
		for(TypeHashMap<String, Object> resultDate : resultListDates) {
			Attr feiertag = doc.createAttribute("feiertag");
			feiertag.setNodeValue("false");
			Element termin = doc.createElement("termin");
			termine.appendChild(termin);
			termin.setAttributeNode(feiertag);
			
			Element datum = doc.createElement("datum");
			datum.appendChild(doc.createTextNode(resultDate.get("StartDate").toString()));
			termin.appendChild(datum);
			
			Element zeitAnfang = doc.createElement("zeitAnfang");
			zeitAnfang.appendChild(doc.createTextNode(resultDate.get("StartTime").toString()));
			termin.appendChild(zeitAnfang);
			
			Element zeitEnde = doc.createElement("zeitEnde");
			zeitEnde.appendChild(doc.createTextNode(resultDate.get("EndTime").toString()));
			termin.appendChild(zeitEnde);
			
			Element dozent = doc.createElement("dozent");
			dozent.appendChild(doc.createTextNode(resultDate.get("Dozent").toString()));
			termin.appendChild(dozent);
			
			Element vorlesung = doc.createElement("vorlesung");
			vorlesung.appendChild(doc.createTextNode(resultDate.get("Subject").toString()));
			termin.appendChild(vorlesung);
			
			Element raum = doc.createElement("raum");
			raum.appendChild(doc.createTextNode(resultDate.get("raum").toString()));
			termin.appendChild(raum);
		}
		
		for(TypeHashMap<String, Object> resultHoliday : resultListHolidays) {
			Attr feiertagYes = doc.createAttribute("feiertag");
			feiertagYes.setNodeValue("true");
			Element termin = doc.createElement("termin");
			termine.appendChild(termin);
			termin.setAttributeNode(feiertagYes);
			
			Element datum = doc.createElement("datum");
			datum.appendChild(doc.createTextNode(resultHoliday.get("Datum").toString()));
			termin.appendChild(datum);
			
			Element feiertagName = doc.createElement("feiertag");
			feiertagName.appendChild(doc.createTextNode(resultHoliday.get("Name").toString()));
			termin.appendChild(feiertagName);
		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(file);
		transformer.transform(source, result);
		} catch (ParserConfigurationException | TransformerException e) {
			throw new WebServiceException(ExceptionStatus.FILE_CREATION_ERROR);
		}
		out.close();
		
		return file;
	}
	
}
