package de.dhbw.vvs.application;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import de.dhbw.vvs.ressources.SaySomething;

/**
 * Main web service application
 */
public class VVSApplication extends Application {
	
	public static String VERSION_ONE = "/v1";
	
	/**
	 * This method routes the different URIs to their corresponding ServerResources
	 */
	@Override
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		
		//EXAMPLE
		router.attach(VERSION_ONE + "/say/{something}", SaySomething.class);
		
		return router;
	}
}
