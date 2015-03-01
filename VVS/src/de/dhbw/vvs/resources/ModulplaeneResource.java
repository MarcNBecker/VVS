package de.dhbw.vvs.resources;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.ResourceException;

import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.model.Modulplan;
import de.dhbw.vvs.utility.JSONify;

public class ModulplaeneResource extends SecureServerResource {
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowGet();
		super.allowPost();
	}
	
	@Override
	protected Object receiveGet() throws WebServiceException {
		return Modulplan.getAll();
	}
	
	@Override
	protected Object receivePost(JsonRepresentation json) throws JSONException, WebServiceException {
		JSONObject jO = json.getJsonObject();
		Modulplan modulplan = JSONify.deserialize(jO.toString(), Modulplan.class);
		int vorlageID = jO.optInt("vorlage");
		if (vorlageID == 0) {
			return modulplan.create();	
		} else {
			Modulplan m = modulplan.create();
			modulplan.enhance(vorlageID);
			return m;
		}
	}
	
}
