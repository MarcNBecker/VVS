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

public class ModulplanResource extends SecureServerResource {
	
	private int modulplanID;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowGet();
		super.allowPut();
		super.allowDelete();
		ConcurrentMap<String, Object> urlAttributes = getRequest().getAttributes();
		try {
			 this.modulplanID = Integer.parseInt(URLDecoder.decode(urlAttributes.get("modulplanID").toString(), "UTF-8"));
		} catch (NumberFormatException | NullPointerException e) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT).toResourceException();
		} catch (UnsupportedEncodingException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
	
	@Override
	protected Object receiveGet() throws WebServiceException {
		// TODO Auto-generated method stub
		return super.receiveGet();
	}
	
	@Override
	protected Object receivePut(JsonRepresentation json) throws JSONException, WebServiceException {
		// TODO Auto-generated method stub
		return super.receivePut(json);
	}
	
	@Override
	protected Object receiveDelete() throws WebServiceException {
		// TODO Auto-generated method stub
		return super.receiveDelete();
	}
	
	public int getModulplanID() {
		return this.modulplanID;
	}
	
}
