package com.lukrzak.ByForest.user;

import com.lukrzak.ByForest.user.controller.DefaultUserController;
import com.lukrzak.ByForest.user.dto.GetUserResponse;
import com.lukrzak.ByForest.user.dto.PostUserRequest;
import com.lukrzak.ByForest.user.exception.CredentialsAlreadyTakenException;
import com.lukrzak.ByForest.user.exception.UserDoesntExistException;
import com.lukrzak.ByForest.user.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultControllerTests {

	private static DefaultUserController userController;
	private static GetUserResponse dummyResponse = new GetUserResponse("login", "email@em.com");
	private static PostUserRequest dummyPostRequest = new PostUserRequest("login", "pass", "email@em.com");

	@BeforeAll
	static void setup() throws UserDoesntExistException, CredentialsAlreadyTakenException {
		UserService userService = mock(UserService.class);
		userController = new DefaultUserController(userService);

		when(userService.findUser(anyLong()))
				.thenAnswer(inv -> {
					Long providedId = inv.getArgument(0);
					if (providedId.equals(1L))
						return dummyResponse;
					throw new UserDoesntExistException("User with id: " + providedId + " does not exist");
				});

		doNothing().when(userService).saveUser(any());
		doThrow(CredentialsAlreadyTakenException.class).when(userService).saveUser(dummyPostRequest);

		doNothing().when(userService).deleteUser(anyLong());
	}

	@Test
	void testFindingUser() {
		ResponseEntity<GetUserResponse> response = userController.findUser(1L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(dummyResponse, response.getBody());
		assertThrows(ResponseStatusException.class, () -> userController.findUser(2L));
		try {
			userController.findUser(2L);
		}
		catch (ResponseStatusException e){
			assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
		}
	}

	@Test
	void testSavingUser() {
		userController.saveUser(new PostUserRequest("aa", "bb", "cc@em.com"));
		assertThrows(ResponseStatusException.class, () -> userController.saveUser(dummyPostRequest));
		try {
			userController.saveUser(dummyPostRequest);
		}
		catch (ResponseStatusException e){
			assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
		}
	}

	@Test
	void testDeletingUser() {
		assertEquals(HttpStatus.OK, userController.deleteUser(1L).getStatusCode());
	}

}
