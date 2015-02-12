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
import de.dhbw.vvs.model.Studiengangsleiter;

public class StudiengangsleiterResource extends SecureServerResource {
	
	private int studiengangsleiterID;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowGet();
		super.allowPut();
		super.allowDelete();
		ConcurrentMap<String, Object> urlAttributes = getRequest().getAttributes();
		try {
			 this.studiengangsleiterID = Integer.parseInt(URLDecoder.decode(urlAttributes.get("studiengangsleiterID").toString(), "UTF-8"));
		} catch (NumberFormatException | NullPointerException e) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT).toResourceException();
		} catch (UnsupportedEncodingException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
	
	@Override
	protected Object receiveGet() throws WebServiceException {
		return Studiengangsleiter.get(getStudiengangsleiterID());
	}
	
	/**
	 * Input JSON {name:"Jörgi Baumgart"}
	 */
	@Override
	protected Object receivePut(JsonRepresentation json) throws JSONException, WebServiceException {
		String name = json.getJsonObject().getString("name");
		return Studiengangsleiter.update(getStudiengangsleiterID(), name);
	}
	
	@Override
	protected void receiveDelete() throws WebServiceException {
		Studiengangsleiter.delete(getStudiengangsleiterID());
	}
	
	public int getStudiengangsleiterID() {
		return this.studiengangsleiterID;
	}
	
}
