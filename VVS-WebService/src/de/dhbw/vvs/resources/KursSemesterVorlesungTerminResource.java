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

public class KursSemesterVorlesungTerminResource extends SecureServerResource {
	
	private int kursID;
	private int semester;
	private int vorlesungsID;
	private int terminID;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowPut();
		super.allowDelete();
		ConcurrentMap<String, Object> urlAttributes = getRequest().getAttributes();
		try {
			 this.kursID = Integer.parseInt(URLDecoder.decode(urlAttributes.get("kursID").toString(), "UTF-8"));
			 this.semester = Integer.parseInt(URLDecoder.decode(urlAttributes.get("semester").toString(), "UTF-8"));
			 this.vorlesungsID = Integer.parseInt(URLDecoder.decode(urlAttributes.get("vorlesungsID").toString(), "UTF-8"));
			 this.terminID = Integer.parseInt(URLDecoder.decode(urlAttributes.get("terminID").toString(), "UTF-8"));
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
	
	public int getKursID() {
		return this.kursID;
	}
	
	public int getSemester() {
		return this.semester;
	}
	
	public int getVorlesungsID() {
		return this.vorlesungsID;
	}
	
	public int getTerminID() {
		return this.terminID;
	}
	
}
