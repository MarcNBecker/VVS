package de.dhbw.vvs.resources;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.model.Dozent;
import de.dhbw.vvs.model.Fach;


/**
 * URI: /dozenten/{dozentID}/faecher
 */
public class DozentFaecherResource extends JsonServerResource {
	
	private int dozentID;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowGet();
		ConcurrentMap<String, Object> urlAttributes = getRequest().getAttributes();
		try {
			 this.dozentID = Integer.parseInt(URLDecoder.decode(urlAttributes.get("dozentID").toString(), "UTF-8"));
		} catch (NumberFormatException | NullPointerException e) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT).toResourceException();
		} catch (UnsupportedEncodingException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
	
	@Override
	protected Object receiveGet() throws WebServiceException {
		Dozent dozent = new Dozent(getDozentID());
		ArrayList<Fach> fachList = dozent.getFachList();
		//Get Timestamp when the Dozent last taught the Fach
		for (Fach f : fachList) {
			dozent.lastHeld(f, true);
		}
		return fachList;
	}
	
	public int getDozentID() {
		return this.dozentID;
	}
	
}
