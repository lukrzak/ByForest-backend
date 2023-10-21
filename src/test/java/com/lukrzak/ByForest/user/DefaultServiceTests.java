package com.lukrzak.ByForest.user;

import com.lukrzak.ByForest.exception.ViolatedConstraintException;
import com.lukrzak.ByForest.user.dto.AuthenticationRequest;
import com.lukrzak.ByForest.user.dto.GetUserResponse;
import com.lukrzak.ByForest.user.dto.PostUserRequest;
import com.lukrzak.ByForest.exception.CredentialsAlreadyTakenException;
import com.lukrzak.ByForest.exception.UserException;
import com.lukrzak.ByForest.user.model.User;
import com.lukrzak.ByForest.user.repository.UserRepository;
import com.lukrzak.ByForest.user.service.DefaultUserService;
import com.lukrzak.ByForest.util.JwtGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultServiceTests {

	private static DefaultUserService userService;

	private static final User dummyUser = UserTestUtils.getDummyUser();

	private static final PostUserRequest newUserPostRequest = UserTestUtils.getNewUserPostRequest();

	private static final PostUserRequest existingUserPostRequest = UserTestUtils.getExistingUserPostRequest();

	private static final AuthenticationRequest existingUserAuthenticationRequest = UserTestUtils.getExistingUserAuthenticationRequest();

	private static final AuthenticationRequest incorrectEmailUserAuthenticationRequest = UserTestUtils.getIncorrectEmailUserAuthenticationRequest();

	private static final AuthenticationRequest incorrectPasswordUserAuthenticationRequest = UserTestUtils.getIncorrectPasswordUserAuthenticationRequest();

	@BeforeAll
	static void setup() {
		UserRepository userRepository = mock(UserRepository.class);
		PasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
		JwtGenerator generator = mock(JwtGenerator.class);
		userService = new DefaultUserService(userRepository, encoder, generator);

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

		when(userRepository.findByEmail(anyString()))
				.thenAnswer(inv -> {
					String providedEmail = inv.getArgument(0);
					if (providedEmail.equals(dummyUser.getEmail()))
						return Optional.of(dummyUser);
					return Optional.empty();
				});

		when(encoder.matches(anyString(), anyString()))
				.thenAnswer(inv -> {
					String providedPassword = inv.getArgument(0);
					return providedPassword.equals(dummyUser.getPassword());
				});

		doReturn("token").when(generator).generateJwtToken(anyString());
	}

	@Test
	void testFindingUser() throws UserException {
		GetUserResponse response = userService.findUser(1L);

		assertEquals(response.getEmail(), dummyUser.getEmail());
		assertEquals(response.getLogin(), dummyUser.getLogin());
		assertThrows(UserException.class, () -> userService.findUser(2L));
	}

	@Test
	void testSavingUser() throws CredentialsAlreadyTakenException, ViolatedConstraintException {
		userService.saveUser(newUserPostRequest);

		assertThrows(CredentialsAlreadyTakenException.class, () -> userService.saveUser(existingUserPostRequest));
	}

	@Test
	void testPasswordEncryptingOnUserCreation() throws CredentialsAlreadyTakenException, ViolatedConstraintException {
		User savedUser = userService.saveUser(newUserPostRequest);

		assertNotEquals(newUserPostRequest.getPassword(), savedUser.getPassword());
	}

	@Test
	void testUserAuthentication() throws UserException {
		String token = userService.authenticateUser(existingUserAuthenticationRequest);

		assertNotNull(token);
		assertThrows(UserException.class, () -> userService.authenticateUser(incorrectEmailUserAuthenticationRequest));
		assertThrows(UserException.class, () -> userService.authenticateUser(incorrectPasswordUserAuthenticationRequest));
	}

	@Test
	void testDeletingUser() {
		// TODO check for auth when implemented
		userService.deleteUser(1L);
		userService.deleteUser(2L);
	}

}
