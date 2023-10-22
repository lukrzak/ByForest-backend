package com.lukrzak.ByForest.user;

import com.lukrzak.ByForest.exception.ViolatedConstraintException;
import com.lukrzak.ByForest.user.dto.AuthenticationRequest;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultServiceTests {

	private static final User correctUser = UserTestUtils.getCorrectUser();

	private static final PostUserRequest newUserPostRequest = UserTestUtils.getNewUserPostRequest();

	private static final PostUserRequest existingUserPostRequest = UserTestUtils.getExistingUserPostRequest();

	private static final AuthenticationRequest existingUserAuthenticationRequest = UserTestUtils.getExistingUserAuthenticationRequest();

	private static final AuthenticationRequest incorrectEmailUserAuthenticationRequest = UserTestUtils.getIncorrectEmailUserAuthenticationRequest();

	private static final AuthenticationRequest incorrectPasswordUserAuthenticationRequest = UserTestUtils.getIncorrectPasswordUserAuthenticationRequest();

	private static DefaultUserService userService;

	@BeforeAll
	static void setup() {
		UserRepository userRepository = mock(UserRepository.class);
		PasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
		JwtGenerator generator = mock(JwtGenerator.class);
		userService = new DefaultUserService(userRepository, encoder, generator);

		doReturn(Optional.empty()).when(userRepository).findByLogin(anyString());
		doReturn(Optional.empty()).when(userRepository).findByLoginOrEmail(anyString(), anyString());
		doReturn(Optional.empty()).when(userRepository).findByEmail(anyString());
		doReturn(false).when(encoder).matches(anyString(), anyString());
		doReturn("token").when(generator).generateJwtToken(anyString());

		when(userRepository.findById(correctUser.getId()))
				.thenReturn(Optional.of(correctUser));
		when(userRepository.findByLoginOrEmail(correctUser.getLogin(), correctUser.getEmail()))
				.thenReturn(Optional.of(correctUser));
		when(userRepository.findByEmail(correctUser.getEmail()))
				.thenReturn(Optional.of(correctUser));
		when(encoder.matches(eq(correctUser.getPassword()), anyString()))
				.thenReturn(true);
	}

	@Test
	void testSavingUser() throws CredentialsAlreadyTakenException, ViolatedConstraintException {
		userService.saveUser(newUserPostRequest);

		assertThrows(CredentialsAlreadyTakenException.class, () -> userService.saveUser(existingUserPostRequest));
	}

	@Test
	void testUserAuthentication() throws UserException {
		String token = userService.authenticateUser(existingUserAuthenticationRequest);

		assertNotNull(token);
		assertThrows(UserException.class, () -> userService.authenticateUser(incorrectEmailUserAuthenticationRequest));
		assertThrows(UserException.class, () -> userService.authenticateUser(incorrectPasswordUserAuthenticationRequest));
	}

}
