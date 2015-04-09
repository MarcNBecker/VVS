package de.dhbw.vvs.resources;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.ConcurrentMap;

import org.json.JSONException;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.ResourceException;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.model.Dozent;
import de.dhbw.vvs.utility.JSONify;

/**
 * URI: /dozenten/{dozentID}
 */
public class DozentResource extends JsonServerResource {
	
	private int dozentID;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowGet();
		super.allowPut();
		super.allowDelete();
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
		return new Dozent(getDozentID()).getDirectAttributes();
	}
	
	@Override
	protected Object receivePut(JsonRepresentation json) throws JSONException, WebServiceException {
		Dozent dozent = JSONify.deserialize(json.getJsonObject().toString(), Dozent.class);
		dozent.setID(getDozentID());
		return dozent.update();
	}
	
	@Override
	protected void receiveDelete() throws WebServiceException {
		new Dozent(getDozentID()).delete();
	}
	
	public int getDozentID() {
		return this.dozentID;
	}
	
}
