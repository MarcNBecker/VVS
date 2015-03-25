package de.dhbw.vvs.resources;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Date;
import java.util.concurrent.ConcurrentMap;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.model.Kurs;
import de.dhbw.vvs.model.Termin;
import de.dhbw.vvs.utility.Utility;

public class TermineKursResource extends JsonServerResource {
	
	private Date datum;
	private int kursID;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowGet();
		ConcurrentMap<String, Object> urlAttributes = getRequest().getAttributes();
		try {
			 this.datum = Utility.stringDate(URLDecoder.decode(urlAttributes.get("datum").toString(), "UTF-8"));
			 this.kursID = Integer.parseInt(URLDecoder.decode(urlAttributes.get("kursID").toString(), "UTF-8")); 
		} catch (NumberFormatException | NullPointerException e) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT).toResourceException();
		} catch (UnsupportedEncodingException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} catch (WebServiceException e) {
			throw e.toResourceException();
		}
	}
	
	@Override
	protected Object receiveGet() throws WebServiceException {
		return Termin.getAllForKursOnDate(getDatum(), new Kurs(getKursID()));
	}
	
	public Date getDatum() {
		return this.datum;
	}	
	
	public int getKursID() {
		return this.kursID;
	}
	
}
