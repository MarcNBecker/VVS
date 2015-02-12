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

public class FeiertagResource extends SecureServerResource {
	
	private int jahr;
	private String datum;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowPut();
		super.allowDelete();
		ConcurrentMap<String, Object> urlAttributes = getRequest().getAttributes();
		try {
			 this.jahr = Integer.parseInt(URLDecoder.decode(urlAttributes.get("jahr").toString(), "UTF-8"));
			 this.datum = URLDecoder.decode(urlAttributes.get("jahr").toString(), "UTF-8");
		} catch (NumberFormatException | NullPointerException e) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT).toResourceException();
		} catch (UnsupportedEncodingException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
	
	@Override
	protected Object receivePut(JsonRepresentation json) throws JSONException, WebServiceException {
		// TODO Auto-generated method stub
		return super.receivePut(json);
	}
	
	@Override
	protected void receiveDelete() throws WebServiceException {
		// TODO Auto-generated method stub
	}
	
	public int getJahr() {
		return this.jahr;
	}
	
	public String getDatum() {
		return this.datum;
	}
	
}
