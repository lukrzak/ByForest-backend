package com.lukrzak.ByForest.user;

import com.lukrzak.ByForest.exception.ViolatedConstraintException;
import com.lukrzak.ByForest.user.controller.DefaultUserController;
import com.lukrzak.ByForest.user.dto.AuthenticationRequest;
import com.lukrzak.ByForest.user.dto.PostUserRequest;
import com.lukrzak.ByForest.exception.CredentialsAlreadyTakenException;
import com.lukrzak.ByForest.exception.UserException;
import com.lukrzak.ByForest.user.model.User;
import com.lukrzak.ByForest.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultControllerTests {

	private final PostUserRequest postUserRequest = PostUserRequest.builder()
			.login("login")
			.email("email@em.com")
			.password("Password!123")
			.build();

	private final AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
			.email("email@em.com")
			.password("Password!123")
			.build();

	private final UserService userService = mock(UserService.class);

	private final DefaultUserController userController = new DefaultUserController(userService);

	@Test
	void testSavingUser() throws ViolatedConstraintException, CredentialsAlreadyTakenException {
		when(userService.saveUser(any()))
				.thenReturn(new User());

		ResponseEntity<String> response = userController.saveUser(postUserRequest);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("User " + postUserRequest + " saved successfully", response.getBody());
	}

	@Test
	void testSavingUserWithTakenCredentials() throws ViolatedConstraintException, CredentialsAlreadyTakenException {
		when(userService.saveUser(any()))
				.thenThrow(CredentialsAlreadyTakenException.class);

		assertThrows(CredentialsAlreadyTakenException.class, () -> userController.saveUser(postUserRequest));
	}

	@Test
	void testUserAuthentication() throws UserException {
		String tokenValue = "token";
		when(userService.authenticateUser(any()))
				.thenReturn(tokenValue);

		ResponseEntity<String> response = userController.authenticateUser(authenticationRequest);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(tokenValue, response.getBody());
	}

	@Test
	void testUserAuthenticationWithIncorrectCredentials() throws UserException {
		when(userService.authenticateUser(any()))
				.thenThrow(UserException.class);

		assertThrows(UserException.class, () -> userController.authenticateUser(authenticationRequest));
	}

}
