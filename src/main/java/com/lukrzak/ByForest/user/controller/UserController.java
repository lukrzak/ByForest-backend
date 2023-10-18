package com.lukrzak.ByForest.user.controller;

import com.lukrzak.ByForest.user.dto.PostUserRequest;
import com.lukrzak.ByForest.exception.CredentialsAlreadyTakenException;
import org.springframework.http.ResponseEntity;

public interface UserController {

	ResponseEntity<String> saveUser(PostUserRequest userRequest) throws CredentialsAlreadyTakenException;

}
