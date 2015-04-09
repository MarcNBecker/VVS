package de.dhbw.vvs.resources;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.ConcurrentMap;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.model.FachInstanz;
import de.dhbw.vvs.model.Kurs;

/**
 * URI: /kurse/{kursID}/vorlesungen/offen
 */
public class KursVorlesungenOffenResource extends JsonServerResource {
	
	private int kursID;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowGet();
		ConcurrentMap<String, Object> urlAttributes = getRequest().getAttributes();
		try {
			 this.kursID = Integer.parseInt(URLDecoder.decode(urlAttributes.get("kursID").toString(), "UTF-8"));
		} catch (NumberFormatException | NullPointerException e) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT).toResourceException();
		} catch (UnsupportedEncodingException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
	
	@Override
	protected Object receiveGet() throws WebServiceException {
		Kurs kurs = new Kurs(getKursID());
		return FachInstanz.getAllMissingForKurs(kurs);
	}
	
	public int getKursID() {
		return this.kursID;
	}
	
}
