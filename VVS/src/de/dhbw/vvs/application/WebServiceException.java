package de.dhbw.vvs.application;

import org.restlet.resource.ResourceException;

/**
 * This class represents an exception of the web service
 */
public class WebServiceException extends Exception {
	
	private static final long serialVersionUID = 6984086281804936921L;

	private ExceptionStatus exceptionStatus;
	
	/**
	 * Constructs a new exception
	 * @param exceptionStatus the status of the exception
	 */
	public WebServiceException(ExceptionStatus exceptionStatus) {
		super(exceptionStatus.toString());
		this.exceptionStatus = exceptionStatus;
	}
	
	/**
	 * Turns the WebServiceException into a ResourceException
	 * @return the ResourceException
	 */
	public ResourceException toResourceException() {
		return new ResourceException(exceptionStatus.getHttpStatus(), exceptionStatus.getErrorCode()+" "+exceptionStatus.toString());
	}
}
