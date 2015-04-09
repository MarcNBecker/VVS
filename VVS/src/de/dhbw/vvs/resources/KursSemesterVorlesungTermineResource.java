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
import de.dhbw.vvs.model.Termin;
import de.dhbw.vvs.model.Vorlesung;
import de.dhbw.vvs.utility.JSONify;

/**
 * URI: /kurse/{kursID}/{semester}/vorlesungen/{vorlesungsID}/termine
 */
public class KursSemesterVorlesungTermineResource extends JsonServerResource {
	
	private int kursID;
	private int semester;
	private int vorlesungsID;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowGet();
		super.allowPost();
		ConcurrentMap<String, Object> urlAttributes = getRequest().getAttributes();
		try {
			 this.kursID = Integer.parseInt(URLDecoder.decode(urlAttributes.get("kursID").toString(), "UTF-8"));
			 this.semester = Integer.parseInt(URLDecoder.decode(urlAttributes.get("semester").toString(), "UTF-8"));
			 this.vorlesungsID = Integer.parseInt(URLDecoder.decode(urlAttributes.get("vorlesungsID").toString(), "UTF-8"));
		} catch (NumberFormatException | NullPointerException e) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT).toResourceException();
		} catch (UnsupportedEncodingException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
	
	@Override
	protected Object receiveGet() throws WebServiceException {
		Vorlesung vorlesung = new Vorlesung(getVorlesungsID());
		vorlesung.setKursID(getKursID());
		vorlesung.setSemester(getSemester());
		return Termin.getAll(vorlesung);
	}
	
	@Override
	protected Object receivePost(JsonRepresentation json) throws JSONException, WebServiceException {
		Vorlesung vorlesung = new Vorlesung(getVorlesungsID());
		vorlesung.setKursID(getKursID());
		vorlesung.setSemester(getSemester());
		vorlesung.getDirectAttributes();
		Termin termin = JSONify.deserialize(json.getJsonObject().toString(), Termin.class);
		termin.setVorlesungID(vorlesung.getID());
		return termin.create();
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
	
}
