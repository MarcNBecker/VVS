package de.dhbw.vvs.resources;

import java.io.IOException;

import org.json.JSONException;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.database.ConnectionPool;
import de.dhbw.vvs.utility.JSONify;

/**
 * A secure ServerResource
 */
public abstract class SecureServerResource extends ServerResource {

	protected boolean allowGet = false;
	protected boolean allowPost = false;
	protected boolean allowPut = false;
	protected boolean allowDelete = false;
	
	/**
	 * This method sets the handling of the response to not negotiated and not conditional.
	 * As a result of this only the not Variant dependant get, post, delete, put, head & options methods of the ServerResource can be called
	 */
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		setNegotiated(false);
		setConditional(false);
	}
	
	/**
	 * Sets the response entity to a JSON object representing a failure if a ResourceException was thrown
	 */
	@Override
	protected void doCatch(Throwable throwable) {
		super.doCatch(throwable);
		if(throwable instanceof ResourceException) {
			ResourceException e = (ResourceException) throwable;
			getResponse().setEntity(new JsonRepresentation(JSONify.serialize(e)));
		}
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
	 * Handles a DELETE request
	 */
	@Override
	protected Representation delete() throws ResourceException {
		if(!allowDelete) {
			throw new WebServiceException(ExceptionStatus.METHOD_NOT_ALLOWED).toResourceException();
		}
		try {
			Object result = receiveDelete();
			return new JsonRepresentation(JSONify.serialize(result));
		} catch (WebServiceException e) {
			throw e.toResourceException();
		}
	}
	
	/**
	 * Is called when a DELETE is received, this can be overwritten
	 * @return an Object to be serialized to JSON
	 * @throws WebServiceException
	 */
	protected Object receiveDelete() throws WebServiceException {
		throw new WebServiceException(ExceptionStatus.METHOD_NOT_ALLOWED).toResourceException();
	}
	
	/**
	 * Close the database connection on releasing the resource
	 */
	@Override
	protected void doRelease() throws ResourceException {
		super.doRelease();
		ConnectionPool.getConnectionPool().closeConnection();
	}
	
}
