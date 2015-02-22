package de.dhbw.vvs.resources;

import org.json.JSONException;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.ResourceException;

import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.model.Studiengangsleiter;
import de.dhbw.vvs.utility.JSONify;

public class StudiengangsleiterKollektionResource extends SecureServerResource {
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowGet();
		super.allowPost();
	}
	
	@Override
	protected Object receiveGet() throws WebServiceException {
		return Studiengangsleiter.getAll();
	}
	
	/**
	 * Input JSON: {name:"Jörg Baumgart"}
	 */
	@Override
	protected Object receivePost(JsonRepresentation json) throws JSONException, WebServiceException {
		Studiengangsleiter studiengangsleiter = JSONify.deserialize(json.getJsonObject().toString(), Studiengangsleiter.class);
		return studiengangsleiter.create();
	}
	
}
