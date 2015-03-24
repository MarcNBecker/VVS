package de.dhbw.vvs.resources;

import java.io.IOException;

import org.json.JSONException;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.utility.JSONify;

/**
 * A secure ServerResource
 */
public abstract class JsonServerResource extends EasyServerResource {

	private boolean allowGet = false;
	private boolean allowPost = false;
	private boolean allowPut = false;
	private boolean allowDelete = false;
	
	/**
	 * Allows a GET request
	 */
	final protected void allowGet() {
		this.allowGet = true;
	}
	
	/**
	 * Handles a GET request
	 */
	@Override
	final protected Representation get() throws ResourceException {
		if(!allowGet) {
			throw new WebServiceException(ExceptionStatus.METHOD_NOT_ALLOWED).toResourceException();
		}
		try {
			Object result = receiveGet();
			getResponse().setStatus(Status.SUCCESS_OK);
			return new JsonRepresentation(JSONify.serialize(result));
		} catch (WebServiceException e) {
			throw e.toResourceException();
		}
	}
	
	/**
	 * Is called when a GET is received, this can be overwritten
	 * @return an Object to be serialized to JSON
	 * @throws WebServiceException
	 */
	protected Object receiveGet() throws WebServiceException {
		throw new WebServiceException(ExceptionStatus.METHOD_NOT_ALLOWED);
	}
	
	/**
	 * Allows a POST request
	 */
	final protected void allowPost() {
		this.allowPost = true;
	}
	
	/**
	 * Handles a POST request
	 */
	@Override
	final protected Representation post(Representation entity) throws ResourceException {
		if(!allowPost) {
			throw new WebServiceException(ExceptionStatus.METHOD_NOT_ALLOWED).toResourceException();
		}
		if(entity.getMediaType().equals(MediaType.APPLICATION_JSON)) {
			try {
				JsonRepresentation jsonRequest = new JsonRepresentation(entity);
				Object result = receivePost(jsonRequest);
				getResponse().setStatus(Status.SUCCESS_CREATED);
				return new JsonRepresentation(JSONify.serialize(result));
			} catch (IOException | JSONException e ) {
				throw new WebServiceException(ExceptionStatus.JSON_PARSING_FAILED).toResourceException();
			} catch (WebServiceException e) {
				throw e.toResourceException();
			}
		} else {
			throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
		}
	}
	
	/**
	 * Is called when a POST is received, this can be overwritten
	 * @return an Object to be serialized to JSON
	 * @throws WebServiceException
	 */
	protected Object receivePost(JsonRepresentation json) throws JSONException, WebServiceException {
		throw new WebServiceException(ExceptionStatus.METHOD_NOT_ALLOWED).toResourceException();
	}
	
	/**
	 * Allows a PUT request
	 */
	final protected void allowPut() {
		this.allowPut = true;
	}
	
	/**
	 * Handles a PUT request
	 */
	@Override
	final protected Representation put(Representation entity) throws ResourceException {
		if(!allowPut) {
			throw new WebServiceException(ExceptionStatus.METHOD_NOT_ALLOWED).toResourceException();
		}
		if(entity.getMediaType().equals(MediaType.APPLICATION_JSON)) {
			try {
				JsonRepresentation jsonRequest = new JsonRepresentation(entity);
				Object result = receivePut(jsonRequest);
				getResponse().setStatus(Status.SUCCESS_OK);
				return new JsonRepresentation(JSONify.serialize(result));
			} catch (IOException | JSONException e ) {
				throw new WebServiceException(ExceptionStatus.JSON_PARSING_FAILED).toResourceException();
			} catch (WebServiceException e) {
				throw e.toResourceException();
			}
		} else {
			throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
		}
	}
	
	/**
	 * Is called when a PUT is received, this can be overwritten
	 * @return an Object to be serialized to JSON
	 * @throws WebServiceException
	 */
	protected Object receivePut(JsonRepresentation json) throws JSONException, WebServiceException {
		throw new WebServiceException(ExceptionStatus.METHOD_NOT_ALLOWED).toResourceException();
	}
	
	/**
	 * Allows a DELETE request
	 */
	final protected void allowDelete() {
		this.allowDelete = true;
	}
	
	/**
	 * Handles a DELETE request
	 */
	@Override
	protected Representation delete() throws ResourceException {
		if(!allowDelete) {
			throw new WebServiceException(ExceptionStatus.METHOD_NOT_ALLOWED).toResourceException();
		}
		try {
			receiveDelete();
			getResponse().setStatus(Status.SUCCESS_NO_CONTENT);
			return new EmptyRepresentation();
		} catch (WebServiceException e) {
			throw e.toResourceException();
		}
	}
	
	/**
	 * Is called when a DELETE is received, this can be overwritten
	 * @throws WebServiceException
	 */
	protected void receiveDelete() throws WebServiceException {
		throw new WebServiceException(ExceptionStatus.METHOD_NOT_ALLOWED).toResourceException();
	}
	
}
