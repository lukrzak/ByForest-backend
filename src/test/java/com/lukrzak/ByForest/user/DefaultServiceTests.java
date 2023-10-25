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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultServiceTests {

	private final PostUserRequest postUserRequest = PostUserRequest.builder()
			.login("login")
			.email("email@em.com")
			.password("Password!123")
			.build();

	private final AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
			.email("email@em.com")
			.password("Password!123")
			.build();

	private final UserRepository userRepository = mock(UserRepository.class);

	private final PasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

	private final JwtGenerator generator = mock(JwtGenerator.class);

	private final DefaultUserService userService = new DefaultUserService(userRepository, encoder, generator);

	@Test
	void testSavingUser() throws CredentialsAlreadyTakenException, ViolatedConstraintException {
		when(userRepository.save(any()))
				.thenReturn(new User());

		userService.saveUser(postUserRequest);
	}

	@Test
	void testSavingUserWithTakenLogin() {
		when(userRepository.findByLoginOrEmail(anyString(), anyString()))
				.thenReturn(Optional.of(new User()));

		assertThrows(CredentialsAlreadyTakenException.class, () -> userService.saveUser(postUserRequest));
	}

	@Test
	void testUserAuthentication() throws UserException {
		when(userRepository.findByEmail(anyString()))
				.thenReturn(Optional.of(new User()));
		when(encoder.matches(anyString(), any()))
				.thenReturn(true);
		when(generator.generateJwtToken(any()))
				.thenReturn("token");

		String token = userService.authenticateUser(authenticationRequest);

		assertNotNull(token);
	}

	@Test
	void testUserAuthenticationWithIncorrectEmail() {
		when(userRepository.findByEmail(anyString()))
				.thenReturn(Optional.empty());

		assertThrows(UserException.class, () -> userService.authenticateUser(authenticationRequest));
	}

	@Test
	void testUserAuthenticationWithIncorrectPassword() {
		when(userRepository.findByEmail(anyString()))
				.thenReturn(Optional.of(new User()));
		when(encoder.matches(anyString(), anyString()))
				.thenReturn(false);

		assertThrows(UserException.class, () -> userService.authenticateUser(authenticationRequest));
	}

}
