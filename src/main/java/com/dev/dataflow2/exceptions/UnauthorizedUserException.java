/**
 * 
 */
package com.dev.dataflow2.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author tonyr
 *
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedUserException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public UnauthorizedUserException(String message) {
		super(message);
	}

}
