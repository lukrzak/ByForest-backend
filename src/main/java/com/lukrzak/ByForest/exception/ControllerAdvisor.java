package com.lukrzak.ByForest.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor {

	@ExceptionHandler(CredentialsAlreadyTakenException.class)
	public final ResponseEntity<String> handleCredentialsAlreadyTakenException(CredentialsAlreadyTakenException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}

	@ExceptionHandler(ViolatedConstraintException.class)
	public final ResponseEntity<String> handleConstraintViolationException(ViolatedConstraintException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}

	@ExceptionHandler(UserDoesntExistException.class)
	public final ResponseEntity<String> handleUserDoesntExistException(UserDoesntExistException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}

}
