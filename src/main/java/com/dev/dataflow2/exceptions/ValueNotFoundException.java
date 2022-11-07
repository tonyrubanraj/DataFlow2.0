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
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ValueNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ValueNotFoundException(String message) {
		super(message);
	}
}
