package com.lukrzak.ByForest.user;

import com.lukrzak.ByForest.exception.ViolatedConstraintException;
import com.lukrzak.ByForest.user.controller.DefaultUserController;
import com.lukrzak.ByForest.user.dto.AuthenticationRequest;
import com.lukrzak.ByForest.user.dto.PostUserRequest;
import com.lukrzak.ByForest.exception.CredentialsAlreadyTakenException;
import com.lukrzak.ByForest.exception.UserException;
import com.lukrzak.ByForest.user.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class DefaultControllerTests {

	private static final PostUserRequest existingUserPostRequest = UserTestUtils.getExistingUserPostRequest();

	private static final PostUserRequest newUserPostRequest = UserTestUtils.getNewUserPostRequest();

	private static final AuthenticationRequest validAuthenticationRequest = UserTestUtils.getExistingUserAuthenticationRequest();

	private static final AuthenticationRequest incorrectEmailUserAuthenticationRequest = UserTestUtils.getIncorrectEmailUserAuthenticationRequest();

	private static DefaultUserController userController;

	@BeforeAll
	static void setup() throws UserException, CredentialsAlreadyTakenException, ViolatedConstraintException {
		UserService userService = mock(UserService.class);
		userController = new DefaultUserController(userService);

		doThrow(CredentialsAlreadyTakenException.class).when(userService).saveUser(existingUserPostRequest);
		doThrow(UserException.class).when(userService).authenticateUser(any());

		doReturn("token").when(userService).authenticateUser(validAuthenticationRequest);
	}

	@Test
	void testSavingUser() throws ViolatedConstraintException, CredentialsAlreadyTakenException {
		ResponseEntity<String> response = userController.saveUser(newUserPostRequest);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("User " + newUserPostRequest + " saved successfully", response.getBody());
		assertThrows(CredentialsAlreadyTakenException.class, () -> userController.saveUser(existingUserPostRequest));
	}

	@Test
	void testUserAuthentication() throws UserException {
		ResponseEntity<String> response = userController.authenticateUser(validAuthenticationRequest);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertThrows(UserException.class, () -> userController.authenticateUser(incorrectEmailUserAuthenticationRequest));
	}

}
