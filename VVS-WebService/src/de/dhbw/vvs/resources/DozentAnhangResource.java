package de.dhbw.vvs.resources;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.ConcurrentMap;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;

public class DozentAnhangResource extends SecureServerResource {
	
	private int dozentID;
	private int anhangID;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowGet();
		super.allowDelete();
		ConcurrentMap<String, Object> urlAttributes = getRequest().getAttributes();
		try {
			 this.dozentID = Integer.parseInt(URLDecoder.decode(urlAttributes.get("dozentID").toString(), "UTF-8"));
			 this.anhangID = Integer.parseInt(URLDecoder.decode(urlAttributes.get("anhangID").toString(), "UTF-8"));
		} catch (NumberFormatException | NullPointerException e) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT).toResourceException();
		} catch (UnsupportedEncodingException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
	
	@Override
	protected Object receiveGet() throws WebServiceException {
		return super.receiveGet();
	}
	
	@Override
	protected void receiveDelete() throws WebServiceException {
		super.receiveDelete();
	}
	
	public int getDozentID() {
		return this.dozentID;
	}
	
	public int getAnhangID() {
		return this.anhangID;
	}
	
}
