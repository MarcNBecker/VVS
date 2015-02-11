package de.dhbw.vvs.resources;

import org.json.JSONException;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.ResourceException;

import de.dhbw.vvs.application.WebServiceException;

public class DozentenResource extends SecureServerResource {
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowGet();
		super.allowPost();
	}
	
	@Override
	protected Object receiveGet() throws WebServiceException {
		// TODO Auto-generated method stub
		return super.receiveGet();
	}
	
	@Override
	protected Object receivePost(JsonRepresentation json) throws JSONException, WebServiceException {
		// TODO Auto-generated method stub
		return super.receivePost(json);
	}
	
}
