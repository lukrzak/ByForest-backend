package com.lukrzak.ByForest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor {

	@ExceptionHandler(CredentialsAlreadyTakenException.class)
	public final ResponseEntity<String> handleCredentialsAlreadyTakenException(CredentialsAlreadyTakenException e){
		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

}
