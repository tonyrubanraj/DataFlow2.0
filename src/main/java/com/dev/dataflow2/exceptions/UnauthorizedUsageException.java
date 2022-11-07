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
public class UnauthorizedUsageException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public UnauthorizedUsageException(String message) {
		super(message);
	}

}
