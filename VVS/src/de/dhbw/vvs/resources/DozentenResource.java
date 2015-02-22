package de.dhbw.vvs.resources;

import org.json.JSONException;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.ResourceException;

import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.model.Dozent;
import de.dhbw.vvs.utility.JSONify;

public class DozentenResource extends SecureServerResource {
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowGet();
		super.allowPost();
	}
	
	@Override
	protected Object receiveGet() throws WebServiceException {
		return Dozent.getAll();
	}
	
	@Override
	protected Object receivePost(JsonRepresentation json) throws JSONException, WebServiceException {
		Dozent dozent = JSONify.deserialize(json.getJsonObject().toString(), Dozent.class);
		return dozent.create();
	}
	
}
