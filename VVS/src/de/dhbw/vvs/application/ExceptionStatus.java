package de.dhbw.vvs.application;

import org.restlet.data.Status;

/**
 * This enum represents all exception status and turns them into internal exception codes and http response status
 */
public enum ExceptionStatus {

	//DATABASE ERRORS
	DB_ESTABLISHING_CONNECTION_FAILED(0, Status.SERVER_ERROR_INTERNAL),
	DB_CLOSING_CONNECTION_FAILED(1, Status.SERVER_ERROR_INTERNAL),
	DB_INVALID_STATEMENT(2, Status.SERVER_ERROR_INTERNAL),
	DB_EXECUTION_FAILED(3, Status.SERVER_ERROR_INTERNAL),
	DB_DUPLICATE_ENTRY(4, Status.CLIENT_ERROR_CONFLICT),
	
	//CASTING ERRORS
	CASTING_NULL(10, Status.SERVER_ERROR_INTERNAL),
	CASTING_BOOLEAN_FAILED(11, Status.SERVER_ERROR_INTERNAL),
	CASTING_INT_FAILED(12, Status.SERVER_ERROR_INTERNAL),
	CASTING_LONG_FAILED(13, Status.SERVER_ERROR_INTERNAL),
	CASTING_STRING_FAILED(14, Status.SERVER_ERROR_INTERNAL),
	
	//RESOURCE ERRORS
	METHOD_NOT_ALLOWED(20, Status.CLIENT_ERROR_METHOD_NOT_ALLOWED),
	INVALID_ARGUMENT(21, Status.CLIENT_ERROR_BAD_REQUEST),
	INVALID_ARGUMENT_STRING(22, Status.CLIENT_ERROR_BAD_REQUEST),
	INVALID_ARGUMENT_ID(23, Status.CLIENT_ERROR_BAD_REQUEST),
	INVALID_ARGUMENT_ENUM(24, Status.CLIENT_ERROR_BAD_REQUEST),
	INVALID_ARGUMENT_MAIL(25, Status.CLIENT_ERROR_BAD_REQUEST),
	INVALID_ARGUMENT_PHONE(26, Status.CLIENT_ERROR_BAD_REQUEST),
	INVALID_ARGUMENT_NUMBER(27, Status.CLIENT_ERROR_BAD_REQUEST),
	INVALID_ARGUMENT_DATE(28, Status.CLIENT_ERROR_BAD_REQUEST),
	INVALID_ARGUMENT_OBJECT(29, Status.CLIENT_ERROR_BAD_REQUEST),
	
	//NOT FOUND ERRORS
	OBJECT_NOT_FOUND(30, Status.CLIENT_ERROR_NOT_FOUND),
	AUTHENTICATION_FAILED(31, Status.CLIENT_ERROR_UNAUTHORIZED),
	
	//JSON ERRORS
	JSON_PARSING_FAILED(40, Status.CLIENT_ERROR_BAD_REQUEST),
	
	FILE_CREATION_ERROR(50, Status.SERVER_ERROR_INSUFFICIENT_STORAGE),
	
	//GENERAL ERRORS
	SHA256_FAILED(99, Status.SERVER_ERROR_INTERNAL);
	

	private int errorCode;
	private Status httpStatus;
	
	/**
	 * Creates a new Exception Status
	 * @param errorCode the internal error code
	 * @param httpStatus the http status
	 */
	private ExceptionStatus(int errorCode, Status httpStatus) {
		this.errorCode = errorCode;
		this.httpStatus = httpStatus;
	}
	
	/**
	 * @return the internal error code
	 */
	public int getErrorCode() {
		return errorCode;
	}
	
	/**
	 * @return the http status
	 */
	public Status getHttpStatus() {
		return httpStatus;
	}
	
}
