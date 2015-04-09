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
 * URI: /dozenten/faecher/{fachID}
 */
public class DozentenFachResource extends JsonServerResource {
	
	private int fachID;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowGet();
		ConcurrentMap<String, Object> urlAttributes = getRequest().getAttributes();
		try {
			 this.fachID = Integer.parseInt(URLDecoder.decode(urlAttributes.get("fachID").toString(), "UTF-8"));
		} catch (NumberFormatException | NullPointerException e) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT).toResourceException();
		} catch (UnsupportedEncodingException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
	
	@Override
	protected Object receiveGet() throws WebServiceException {
		Fach fach = new Fach(getFachID());
		ArrayList<Dozent> dozentList = Dozent.getAllForFach(fach);
		//Get Timestamp when the Dozent last taught the Fach
		for (Dozent d : dozentList) {
			d.lastHeld(fach, false);
		}
		return dozentList;
	}
	
	public int getFachID() {
		return fachID;
	}
	
}
