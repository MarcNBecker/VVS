package de.dhbw.vvs.resources;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Date;
import java.util.concurrent.ConcurrentMap;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.model.Dozent;
import de.dhbw.vvs.model.Termin;
import de.dhbw.vvs.utility.Utility;

public class TermineDozentResource extends SecureServerResource {
	
	private Date datum;
	private int dozentID;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowGet();
		ConcurrentMap<String, Object> urlAttributes = getRequest().getAttributes();
		try {
			 this.datum = Utility.stringDate(URLDecoder.decode(urlAttributes.get("datum").toString(), "UTF-8"));
			 this.dozentID = Integer.parseInt(URLDecoder.decode(urlAttributes.get("dozentID").toString(), "UTF-8")); 
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
		return Termin.getAllForDozentOnDate(getDatum(), new Dozent(getDozentID()));
	}
	
	public Date getDatum() {
		return this.datum;
	}	
	
	public int getDozentID() {
		return this.dozentID;
	}
	
}
