package com.lukrzak.ByForest.user;

import com.lukrzak.ByForest.exception.ViolatedConstraintException;
import com.lukrzak.ByForest.user.controller.DefaultUserController;
import com.lukrzak.ByForest.user.dto.GetUserResponse;
import com.lukrzak.ByForest.user.dto.PostUserRequest;
import com.lukrzak.ByForest.exception.CredentialsAlreadyTakenException;
import com.lukrzak.ByForest.exception.UserDoesntExistException;
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
	private static final GetUserResponse dummyResponse = new GetUserResponse("login", "email@em.com");
	private static final PostUserRequest dummyPostRequest = new PostUserRequest("login", "pass", "email@em.com");

	@BeforeAll
	static void setup() throws UserDoesntExistException, CredentialsAlreadyTakenException, ViolatedConstraintException {
		UserService userService = mock(UserService.class);
		userController = new DefaultUserController(userService);

		when(userService.findUser(anyLong()))
				.thenAnswer(inv -> {
					Long providedId = inv.getArgument(0);
					if (providedId.equals(1L))
						return dummyResponse;
					throw new UserDoesntExistException("User with id: " + providedId + " does not exist");
				});

		doReturn(new User()).when(userService).saveUser(any());
		doThrow(CredentialsAlreadyTakenException.class).when(userService).saveUser(dummyPostRequest);

		doNothing().when(userService).deleteUser(anyLong());
	}

	@Test
	void testSavingUser() throws ViolatedConstraintException, CredentialsAlreadyTakenException {
		userController.saveUser(new PostUserRequest("AAA", "Password!123", "email@em.com"));
		assertThrows(CredentialsAlreadyTakenException.class, () -> userController.saveUser(dummyPostRequest));
	}

}
