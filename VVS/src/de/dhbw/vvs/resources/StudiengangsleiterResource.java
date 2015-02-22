package de.dhbw.vvs.resources;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.ConcurrentMap;

import org.json.JSONException;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.ResourceException;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.model.Studiengangsleiter;
import de.dhbw.vvs.utility.JSONify;

public class StudiengangsleiterResource extends SecureServerResource {
	
	private int studiengangsleiterID;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowGet();
		super.allowPut();
		super.allowDelete();
		ConcurrentMap<String, Object> urlAttributes = getRequest().getAttributes();
		try {
			 this.studiengangsleiterID = Integer.parseInt(URLDecoder.decode(urlAttributes.get("studiengangsleiterID").toString(), "UTF-8"));
		} catch (NumberFormatException | NullPointerException e) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT).toResourceException();
		} catch (UnsupportedEncodingException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
	
	@Override
	protected Object receiveGet() throws WebServiceException {
		return new Studiengangsleiter(studiengangsleiterID).getDirectAttributes();
	}
	
	/**
	 * Input JSON {name:"Jörg Baumgart"}
	 */
	@Override
	protected Object receivePut(JsonRepresentation json) throws JSONException, WebServiceException {
		Studiengangsleiter studiengangsleiter = JSONify.deserialize(json.getJsonObject().toString(), Studiengangsleiter.class);
		studiengangsleiter.setID(getStudiengangsleiterID());
		return studiengangsleiter.update();
	}
	
	@Override
	protected void receiveDelete() throws WebServiceException {
		new Studiengangsleiter(getStudiengangsleiterID()).delete();
	}
	
	public int getStudiengangsleiterID() {
		return this.studiengangsleiterID;
	}
	
}
