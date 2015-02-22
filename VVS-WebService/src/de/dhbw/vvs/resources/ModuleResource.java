package de.dhbw.vvs.resources;

import org.restlet.resource.ResourceException;

import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.model.Modul;

public class ModuleResource extends SecureServerResource {
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		super.allowGet();
	}
	
	@Override
	protected Object receiveGet() throws WebServiceException {
		return Modul.getAll();
	}
	
}
