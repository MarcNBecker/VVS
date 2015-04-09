package de.dhbw.vvs.resources;

import org.restlet.data.CacheDirective;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import de.dhbw.vvs.database.ConnectionPool;
import de.dhbw.vvs.utility.JSONify;

/**
 * Handles general settings and transforms exceptions for all ServerResources
 */
public class EasyServerResource extends ServerResource {
	
	/**
	 * This method sets the handling of the response to not negotiated and not conditional.
	 * As a result of this only the not Variant dependant get, post, delete, put, head & options methods of the ServerResource can be called
	 */
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		setNegotiated(false);
		setConditional(false);
		//Don't allow caching
		getResponse().getCacheDirectives().add(CacheDirective.noCache());
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
	 * Close the database connection on releasing the resource
	 */
	@Override
	protected void doRelease() throws ResourceException {
		super.doRelease();
		ConnectionPool.getConnectionPool().closeConnection();
	}

}
