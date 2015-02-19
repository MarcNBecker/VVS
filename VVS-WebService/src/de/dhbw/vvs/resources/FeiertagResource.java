package de.dhbw.vvs.resources;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Date;
import java.util.Calendar;
import java.util.concurrent.ConcurrentMap;

import org.json.JSONException;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.ResourceException;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.model.Feiertag;
import de.dhbw.vvs.utility.JSONify;
import de.dhbw.vvs.utility.Utility;

public class FeiertagResource extends SecureServerResource {
	
	private int jahr;
	private Date datum;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowPut();
		super.allowDelete();
		ConcurrentMap<String, Object> urlAttributes = getRequest().getAttributes();
		try {
			 this.jahr = Integer.parseInt(URLDecoder.decode(urlAttributes.get("jahr").toString(), "UTF-8"));
			 this.datum = Utility.stringDate(URLDecoder.decode(urlAttributes.get("jahr").toString(), "UTF-8"));
		} catch (NumberFormatException | NullPointerException e) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT).toResourceException();
		} catch (UnsupportedEncodingException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} catch (WebServiceException e) {
			throw e.toResourceException();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(getDatum());
		if (c.get(Calendar.YEAR) != jahr) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT_NUMBER).toResourceException();
		}
	}
	
	@Override
	protected Object receivePut(JsonRepresentation json) throws JSONException, WebServiceException {
		Feiertag feiertag = JSONify.deserialize(json.getJsonObject().toString(), Feiertag.class);
		feiertag.setDatum(getDatum());
		return feiertag.update();
	}
	
	@Override
	protected void receiveDelete() throws WebServiceException {
		new Feiertag(getDatum()).delete();
	}
	
	public int getJahr() {
		return this.jahr;
	}
	
	public Date getDatum() {
		return this.datum;
	}
	
}
