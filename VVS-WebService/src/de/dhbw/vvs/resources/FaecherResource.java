package de.dhbw.vvs.resources;

import org.restlet.resource.ResourceException;

import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.model.Fach;

public class FaecherResource extends SecureServerResource {
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowGet();
	}
	
	@Override
	protected Object receiveGet() throws WebServiceException {
		return Fach.getAll();
	}
	
}
