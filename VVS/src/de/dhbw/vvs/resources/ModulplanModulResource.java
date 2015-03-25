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
import de.dhbw.vvs.model.ModulInstanz;
import de.dhbw.vvs.utility.JSONify;

public class ModulplanModulResource extends JsonServerResource {
	
	private int modulplanID;
	private int modulID;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowPut();
		super.allowDelete();
		ConcurrentMap<String, Object> urlAttributes = getRequest().getAttributes();
		try {
			 this.modulplanID = Integer.parseInt(URLDecoder.decode(urlAttributes.get("modulplanID").toString(), "UTF-8"));
			 this.modulID = Integer.parseInt(URLDecoder.decode(urlAttributes.get("modulID").toString(), "UTF-8"));
		} catch (NumberFormatException | NullPointerException e) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT).toResourceException();
		} catch (UnsupportedEncodingException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
	
	@Override
	protected Object receivePut(JsonRepresentation json) throws JSONException, WebServiceException {
		ModulInstanz modulInstanz = JSONify.deserialize(json.getJsonObject().toString(), ModulInstanz.class);
		modulInstanz.setModulplanID(getModulplanID());
		modulInstanz.getModul().setID(getModulID());
		return modulInstanz.create();
	}
	
	@Override
	protected void receiveDelete() throws WebServiceException {
		ModulInstanz.getSingle(getModulplanID(), getModulID()).delete();
	}
	
	public int getModulplanID() {
		return this.modulplanID;
	}
	
	public int getModulID() {
		return this.modulID;
	}
	
}
