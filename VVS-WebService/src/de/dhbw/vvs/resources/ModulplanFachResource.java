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

public class ModulplanFachResource extends SecureServerResource {
	
	private int modulplanID;
	private int modulID;
	private int fachID;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowPost();
		ConcurrentMap<String, Object> urlAttributes = getRequest().getAttributes();
		try {
			 this.modulplanID = Integer.parseInt(URLDecoder.decode(urlAttributes.get("modulplanID").toString(), "UTF-8"));
			 this.modulID = Integer.parseInt(URLDecoder.decode(urlAttributes.get("modulID").toString(), "UTF-8"));
			 this.fachID = Integer.parseInt(URLDecoder.decode(urlAttributes.get("fachID").toString(), "UTF-8"));
		} catch (NumberFormatException | NullPointerException e) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT).toResourceException();
		} catch (UnsupportedEncodingException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	@Override
	protected Object receivePost(JsonRepresentation json) throws JSONException, WebServiceException {
		// TODO Auto-generated method stub
		return super.receivePost(json);
	}
	
	public int getModulplanID() {
		return this.modulplanID;
	}
	
	public int getModulID() {
		return this.modulID;
	}
	
	public int getFachID() {
		return this.fachID;
	}
	
}
