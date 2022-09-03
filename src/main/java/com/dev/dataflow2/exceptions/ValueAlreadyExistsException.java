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
@ResponseStatus(HttpStatus.CONFLICT)
public class ValueAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ValueAlreadyExistsException(String message){
		super(message);
	}
}
