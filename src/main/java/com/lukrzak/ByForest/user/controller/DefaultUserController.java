package com.lukrzak.ByForest.user.controller;

import com.lukrzak.ByForest.exception.UserDoesntExistException;
import com.lukrzak.ByForest.exception.ViolatedConstraintException;
import com.lukrzak.ByForest.user.dto.AuthenticationRequest;
import com.lukrzak.ByForest.user.dto.PostUserRequest;
import com.lukrzak.ByForest.exception.CredentialsAlreadyTakenException;
import com.lukrzak.ByForest.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.lukrzak.ByForest.ByForestApplication.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/users")
@RequiredArgsConstructor
public class DefaultUserController implements UserController {

	private final UserService userService;

	@Override
	@PostMapping
	public ResponseEntity<String> saveUser(@RequestBody PostUserRequest userRequest) throws CredentialsAlreadyTakenException, ViolatedConstraintException {
		userService.saveUser(userRequest);
		return new ResponseEntity<>("User " + userRequest + " saved successfully", HttpStatus.OK);
	}

	@Override
	@PostMapping("/authenticate")
	public ResponseEntity<String> authenticateUser(@RequestBody AuthenticationRequest authenticationRequest) throws UserDoesntExistException {
		return new ResponseEntity<>(userService.authenticateUser(authenticationRequest), HttpStatus.OK);
		//return ResponseEntity.ok().body(userService.authenticateUser(authenticationRequest));
	}

}
