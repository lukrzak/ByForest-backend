package com.lukrzak.ByForest.user;

import com.lukrzak.ByForest.user.dto.GetUserResponse;
import com.lukrzak.ByForest.user.dto.PostUserRequest;
import com.lukrzak.ByForest.user.exception.CredentialsAlreadyTakenException;
import com.lukrzak.ByForest.user.exception.UserDoesntExistException;
import com.lukrzak.ByForest.user.model.User;
import com.lukrzak.ByForest.user.repository.UserRepository;
import com.lukrzak.ByForest.user.service.DefaultUserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultServiceTests {

	private static DefaultUserService userService;
	private static final User dummyUser = new User(1L, "login", "password", "email@em.com");

	@BeforeAll
	static void setup() {
		UserRepository userRepository = mock(UserRepository.class);
		userService = new DefaultUserService(userRepository);

		when(userRepository.findById(anyLong()))
				.thenAnswer(inv -> {
					Long providedId = inv.getArgument(0);
					if (providedId.equals(dummyUser.getId()))
						return Optional.of(dummyUser);
					return Optional.empty();
				});

		when(userRepository.findByLoginOrEmail(anyString(), anyString()))
				.thenAnswer(inv -> {
					String providedLogin = inv.getArgument(0);
					String providedEmail = inv.getArgument(1);
					if (providedEmail.equals(dummyUser.getEmail()) && providedLogin.equals(dummyUser.getLogin()))
						return Optional.of(dummyUser);
					return Optional.empty();
				});
	}

	@Test
	void testFindingUser() throws UserDoesntExistException {
		GetUserResponse response = userService.findUser(1L);

		assertEquals(response.getEmail(), dummyUser.getEmail());
		assertEquals(response.getLogin(), dummyUser.getLogin());
		assertThrows(UserDoesntExistException.class, () -> userService.findUser(2L));
	}

	@Test
	void testSavingUser() throws CredentialsAlreadyTakenException {
		PostUserRequest req = new PostUserRequest("new", "pass", "emmm@em.com");
		PostUserRequest postWithTakenCredentials = new PostUserRequest(dummyUser.getLogin(), "pass", dummyUser.getEmail());

		userService.saveUser(req);

		assertThrows(CredentialsAlreadyTakenException.class, () -> userService.saveUser(postWithTakenCredentials));
	}

	@Test
	void testDeletingUser() {
		// TODO check for auth when implemented
		userService.deleteUser(1L);
		userService.deleteUser(2L);
	}
}
