package de.dhbw.vvs.resources;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.ConcurrentMap;

import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.model.GroupE;
import de.dhbw.vvs.model.Kurs;

public class GroupEResource extends EasyServerResource {
	
	private int kursID;
	private int semester;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		ConcurrentMap<String, Object> urlAttributes = getRequest().getAttributes();
		try {
			 this.kursID = Integer.parseInt(URLDecoder.decode(urlAttributes.get("kursID").toString(), "UTF-8"));
			 this.semester = Integer.parseInt(URLDecoder.decode(urlAttributes.get("semester").toString(), "UTF-8"));
		} catch (NumberFormatException | NullPointerException e) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT).toResourceException();
		} catch (UnsupportedEncodingException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
	
	@Override
	protected Representation get() throws ResourceException {
		try {
			File csv = GroupE.getCSVData(new Kurs(getKursID()), getSemester());
			FileRepresentation representation = new FileRepresentation(csv, new MediaType("text", "csv"));
			representation.setAutoDeleting(true);
			representation.getDisposition().setType(Disposition.TYPE_ATTACHMENT);
			return representation;
		} catch (WebServiceException e) {
			throw e.toResourceException();
		}
	}
	
	public int getKursID() {
		return kursID;
	}
	
	public int getSemester() {
		return semester;
	}

}