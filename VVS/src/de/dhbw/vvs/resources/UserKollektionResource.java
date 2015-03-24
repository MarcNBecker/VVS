package de.dhbw.vvs.resources;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.ResourceException;

import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.model.Studiengangsleiter;
import de.dhbw.vvs.model.User;
import de.dhbw.vvs.utility.JSONify;

public class UserKollektionResource extends JsonServerResource {

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowGet();
		super.allowPost();
	}
	
	@Override
	protected Object receiveGet() throws WebServiceException {
		return User.getAll();
	}
	
	@Override
	protected Object receivePost(JsonRepresentation json) throws JSONException, WebServiceException {
		JSONObject jO = json.getJsonObject();
		User user = JSONify.deserialize(jO.toString(), User.class);
		user.create();
		if(jO.has("istStudiengangsleiter") && jO.getBoolean("istStudiengangsleiter")) {
			new Studiengangsleiter(user.getRepraesentiert()).setIst(user);
		}
		return user;
	}
	
}
