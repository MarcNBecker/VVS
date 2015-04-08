package de.dhbw.vvs.model;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
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
import de.dhbw.vvs.utility.Utility;

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

		Modulplan modulplan = new Modulplan(kurs.getModulplanID()).getDirectAttributes();
		Studiengangsleiter studiengangsleiter = new Studiengangsleiter(kurs.getStudiengangsleiterID()).getDirectAttributes();
		ArrayList<Blocklage> blocklageList = Blocklage.getAll(kurs);
		Blocklage blocklage = null;
		for(Blocklage b: blocklageList) {
			if(b.getSemester() == semester) {
				blocklage = b;
				break;
			}
		}
		
		if(blocklage == null) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT);
		}
		
		// Create XML
		DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
		DocumentBuilder build;
		try {
			build = dFact.newDocumentBuilder();	
		} catch (ParserConfigurationException e) {
			throw new WebServiceException(ExceptionStatus.FILE_CREATION_ERROR);
		}
        Document doc = build.newDocument();
        Element semesterplan = doc.createElement("Semesterplan");
        doc.appendChild(semesterplan);
        
        Element kopfdaten = doc.createElement("Kopfdaten");
        semesterplan.appendChild(kopfdaten);
        
        Element studiengang = doc.createElement("studiengang");
        studiengang.appendChild(doc.createTextNode(modulplan.getStudiengang()));
        kopfdaten.appendChild(studiengang);
		
        Element semesterXML = doc.createElement("semester");
		semesterXML.appendChild(doc.createTextNode(String.valueOf(semester)));
		kopfdaten.appendChild(semesterXML);
		
		Element studienjahrgang = doc.createElement("studienjahrgang");
        studienjahrgang.appendChild(doc.createTextNode(kurs.getKursname()));
        kopfdaten.appendChild(studienjahrgang);
		
        Element studiengangsleiterXML = doc.createElement("studiengangsleiter");
		studiengangsleiterXML.appendChild(doc.createTextNode(studiengangsleiter.getName()));
		kopfdaten.appendChild(studiengangsleiterXML);
		
		Element semesterBeginn = doc.createElement("semesterBeginn");
		semesterBeginn.appendChild(doc.createTextNode(Utility.dateString(blocklage.getStartDatum())));
		kopfdaten.appendChild(semesterBeginn);
		
		Element semesterEnde = doc.createElement("semesterEnde");
		semesterEnde.appendChild(doc.createTextNode(Utility.dateString(blocklage.getEndDatum())));
		kopfdaten.appendChild(semesterEnde);
		
		Element anzahlStudierende = doc.createElement("anzahlStudierende");
		anzahlStudierende.appendChild(doc.createTextNode(String.valueOf(kurs.getStudentenAnzahl())));
		kopfdaten.appendChild(anzahlStudierende);
		
		Element raumHead = doc.createElement("raum");
		raumHead.appendChild(doc.createTextNode(blocklage.getRaum()));
		kopfdaten.appendChild(raumHead);
		
		Element sekretariat = doc.createElement("sekretariat");
		sekretariat.appendChild(doc.createTextNode(kurs.getSekretariatName()));
		kopfdaten.appendChild(sekretariat);
		
		Element vorlesungen = doc.createElement("Vorlesungen");
		semesterplan.appendChild(vorlesungen);
			
		ArrayList<Vorlesung> vorlesungList = Vorlesung.getAll(kurs, semester);
		TreeMap<String, Element> map = new TreeMap<String, Element>();
		for(Vorlesung v: vorlesungList) {
			Element vorlesung = doc.createElement("Vorlesung");
			
			Element fach = doc.createElement("fach");
			fach.appendChild(doc.createTextNode(v.getFachInstanz().getFach().getName()));
			vorlesung.appendChild(fach);
			
			Dozent dozent = new Dozent(v.getDozentID()).getDirectAttributes();
			Element dozentXML = doc.createElement("Dozent");
			vorlesung.appendChild(dozentXML);
			
			Element dozentVorname = doc.createElement("vorname");
			dozentVorname.appendChild(doc.createTextNode(dozent.getVorname()));
			dozentXML.appendChild(dozentVorname);
			
			Element dozentName = doc.createElement("name");
			dozentName.appendChild(doc.createTextNode(dozent.getName()));
			dozentXML.appendChild(dozentName);
			
			Element termine = doc.createElement("Termine");
			vorlesung.appendChild(termine);
			
			ArrayList<Termin> terminList = Termin.getAll(v);
			for(Termin t: terminList) {
				Element termin = doc.createElement("Termin");
				termine.appendChild(termin);
				
				Attr klausur = doc.createAttribute("klausur");
				klausur.setNodeValue(String.valueOf(t.getKlausur()));
				termin.setAttributeNode(klausur);
				
				Element datum = doc.createElement("datum");
				datum.appendChild(doc.createTextNode(Utility.dateString(t.getDatum())));
				termin.appendChild(datum);
				
				Element startUhrzeit = doc.createElement("startUhrzeit");
				startUhrzeit.appendChild(doc.createTextNode(Utility.timeString(t.getStartUhrzeit())));
				termin.appendChild(startUhrzeit);
				
				Element endUhrzeit = doc.createElement("endUhrzeit");
				endUhrzeit.appendChild(doc.createTextNode(Utility.timeString(t.getEndUhrzeit())));
				termin.appendChild(endUhrzeit);
				
				Element raum = doc.createElement("raum");
				raum.appendChild(doc.createTextNode(t.getRaum()));
				termin.appendChild(raum);
				
				Element pause = doc.createElement("pause");
				pause.appendChild(doc.createTextNode(String.valueOf(t.getPause())));
				termin.appendChild(pause);
			}
			
			map.put(dozent.getName() + ", " + dozent.getVorname(), vorlesung);
		}
		
		for(String key : map.keySet()) {
			vorlesungen.appendChild(map.get(key));
		}
		
		Element feiertage = doc.createElement("Feiertage");
		semesterplan.appendChild(feiertage);
		
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<Object> fieldValues = new ArrayList<Object>();
		fieldValues.add(Utility.dateString(blocklage.getStartDatum()));
		fieldValues.add(Utility.dateString(blocklage.getEndDatum()));
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT datum, name FROM feiertag WHERE (datum BETWEEN ? AND ?) ORDER BY datum ASC", fieldValues);
		for(TypeHashMap<String, Object> result : resultList) {
			Element feiertag = doc.createElement("Feiertag");
			feiertage.appendChild(feiertag);
			
			Element datum = doc.createElement("datum");
			datum.appendChild(doc.createTextNode(Utility.dateString((Date) result.get("datum"))));
			feiertag.appendChild(datum);
			
			Element name = doc.createElement("feiertagName");
			name.appendChild(doc.createTextNode(result.getString(name)));
			feiertag.appendChild(name);
		}
		
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new WebServiceException(ExceptionStatus.FILE_CREATION_ERROR);
		}
		
		return file;
	}
	
}
