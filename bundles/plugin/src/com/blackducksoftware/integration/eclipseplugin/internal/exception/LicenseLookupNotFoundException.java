package com.blackducksoftware.integration.eclipseplugin.internal.exception;

public class LicenseLookupNotFoundException extends Exception {

	public LicenseLookupNotFoundException() {
		super();
	}
	
	public LicenseLookupNotFoundException(String message) {
		super(message);
	}
	
	public LicenseLookupNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public LicenseLookupNotFoundException(Throwable cause) {
		super(cause);
	}
	
	public LicenseLookupNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
