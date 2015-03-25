package de.dhbw.vvs.resources;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.ConcurrentMap;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.model.FachInstanz;

public class ModulplanQuickDeleteFachInstanzResource extends JsonServerResource {
	
	private int fachInstanzID;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowDelete();
		ConcurrentMap<String, Object> urlAttributes = getRequest().getAttributes();
		try {
			 this.fachInstanzID = Integer.parseInt(URLDecoder.decode(urlAttributes.get("fachInstanzID").toString(), "UTF-8"));
		} catch (NumberFormatException | NullPointerException e) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT).toResourceException();
		} catch (UnsupportedEncodingException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
	
	@Override
	protected void receiveDelete() throws WebServiceException {
		new FachInstanz(getFachInstanzID()).delete();
	}
	
	public int getFachInstanzID() {
		return this.fachInstanzID;
	}
	
}
