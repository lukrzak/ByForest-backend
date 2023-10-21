package com.lukrzak.ByForest.user;

import com.lukrzak.ByForest.exception.ViolatedConstraintException;
import com.lukrzak.ByForest.user.controller.DefaultUserController;
import com.lukrzak.ByForest.user.dto.AuthenticationRequest;
import com.lukrzak.ByForest.user.dto.GetUserResponse;
import com.lukrzak.ByForest.user.dto.PostUserRequest;
import com.lukrzak.ByForest.exception.CredentialsAlreadyTakenException;
import com.lukrzak.ByForest.exception.UserException;
import com.lukrzak.ByForest.user.model.User;
import com.lukrzak.ByForest.user.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultControllerTests {

	private static DefaultUserController userController;

	private static final GetUserResponse getUserResponse = UserTestUtils.getGetUserResponse();

	private static final PostUserRequest existingUserPostRequest = UserTestUtils.getExistingUserPostRequest();

	private static final PostUserRequest newUserPostRequest = UserTestUtils.getNewUserPostRequest();

	private static final AuthenticationRequest validAuthenticationRequest = UserTestUtils.getExistingUserAuthenticationRequest();

	private static final AuthenticationRequest incorrectEmailUserAuthenticationRequest = UserTestUtils.getIncorrectEmailUserAuthenticationRequest();

	@BeforeAll
	static void setup() throws UserException, CredentialsAlreadyTakenException, ViolatedConstraintException {
		UserService userService = mock(UserService.class);
		userController = new DefaultUserController(userService);

		when(userService.findUser(anyLong()))
				.thenAnswer(inv -> {
					Long providedId = inv.getArgument(0);
					if (providedId.equals(1L))
						return getUserResponse;
					throw new UserException("User with id: " + providedId + " does not exist");
				});

		doReturn(new User()).when(userService).saveUser(any());
		doThrow(CredentialsAlreadyTakenException.class).when(userService).saveUser(existingUserPostRequest);
		doThrow(UserException.class).when(userService).authenticateUser(any());
		doReturn("token").when(userService).authenticateUser(validAuthenticationRequest);
		doNothing().when(userService).deleteUser(anyLong());
	}

	@Test
	void testSavingUser() throws ViolatedConstraintException, CredentialsAlreadyTakenException {
		userController.saveUser(newUserPostRequest);
		assertThrows(CredentialsAlreadyTakenException.class, () -> userController.saveUser(existingUserPostRequest));
	}

	@Test
	void testUserAuthentication() throws UserException {
		userController.authenticateUser(validAuthenticationRequest);
		assertThrows(UserException.class, () -> userController.authenticateUser(incorrectEmailUserAuthenticationRequest));
	}
}
