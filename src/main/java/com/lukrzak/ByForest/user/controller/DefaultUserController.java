package com.lukrzak.ByForest.user.controller;

import com.lukrzak.ByForest.user.dto.GetUserResponse;
import com.lukrzak.ByForest.user.dto.PostUserRequest;
import com.lukrzak.ByForest.user.exception.CredentialsAlreadyTakenException;
import com.lukrzak.ByForest.user.exception.UserDoesntExistException;
import com.lukrzak.ByForest.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static com.lukrzak.ByForest.ByForestApplication.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/users")
@RequiredArgsConstructor
public class DefaultUserController implements UserController {

	private final UserService userService;

	@Override
	public ResponseEntity<GetUserResponse> findUser(long id) {
		GetUserResponse response;
		try{
			response = userService.findUser(id);
		}
		catch (UserDoesntExistException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id: " + id + " does not exist");
		}

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> saveUser(PostUserRequest userRequest) {
		try{
			userService.saveUser(userRequest);
		}
		catch (CredentialsAlreadyTakenException e) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"User with login: " + userRequest.getLogin() + "or email: " + userRequest.getEmail() + " already exists"
			);
		}

		return new ResponseEntity<>("User " + userRequest + " saved successfully", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> deleteUser(long id) {
		userService.deleteUser(id);

		return new ResponseEntity<>("User with id: " + id + " has been deleted", HttpStatus.OK);
	}
}
