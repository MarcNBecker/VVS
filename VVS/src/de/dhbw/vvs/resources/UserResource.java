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
import de.dhbw.vvs.model.User;
import de.dhbw.vvs.utility.JSONify;

public class UserResource extends SecureServerResource {

	private String name;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowPost();
		super.allowPut();
		super.allowDelete();
		ConcurrentMap<String, Object> urlAttributes = getRequest().getAttributes();
		try {
			 this.name = URLDecoder.decode(urlAttributes.get("name").toString(), "UTF-8");
		} catch (NullPointerException e) {
			throw new WebServiceException(ExceptionStatus.INVALID_ARGUMENT).toResourceException();
		} catch (UnsupportedEncodingException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
	
	@Override
	protected Object receivePost(JsonRepresentation json) throws JSONException, WebServiceException {
		User user = JSONify.deserialize(json.getJsonObject().toString(), User.class);
		user.setName(getName());
		return user.authenticate();
	}
	
	@Override
	protected Object receivePut(JsonRepresentation json) throws JSONException, WebServiceException {
		User user = JSONify.deserialize(json.getJsonObject().toString(), User.class);
		user.setName(getName());
		return user.update();
	}
	
	@Override
	protected void receiveDelete() throws WebServiceException {
		User user = new User(getName());
		user.delete();
	}
	
	public String getName() {
		return name;
	}
	
}
