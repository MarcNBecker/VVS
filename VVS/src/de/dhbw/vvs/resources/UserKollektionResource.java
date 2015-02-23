package de.dhbw.vvs.resources;

import org.json.JSONException;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.ResourceException;

import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.model.User;
import de.dhbw.vvs.utility.JSONify;

public class UserKollektionResource extends SecureServerResource {

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowPost();
	}
	
	@Override
	protected Object receivePost(JsonRepresentation json) throws JSONException, WebServiceException {
		User user = JSONify.deserialize(json.getJsonObject().toString(), User.class);
		return user.create();
	}
	
}
