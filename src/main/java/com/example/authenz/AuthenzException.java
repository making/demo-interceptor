package com.example.authenz;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AuthenzException extends RuntimeException {
	public AuthenzException() {
	}

	public AuthenzException(String message) {
		super(message);
	}
}
