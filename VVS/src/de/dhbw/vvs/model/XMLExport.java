package de.dhbw.vvs.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;

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
		//TODO DO XML STUFF HERE
		out.close();
		return file;
	}
	
}
