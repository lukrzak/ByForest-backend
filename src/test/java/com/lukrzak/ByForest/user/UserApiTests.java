package com.lukrzak.ByForest.user;

import com.lukrzak.ByForest.user.dto.AuthenticationRequest;
import com.lukrzak.ByForest.user.dto.PostUserRequest;
import com.lukrzak.ByForest.user.model.User;
import com.lukrzak.ByForest.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static com.lukrzak.ByForest.user.UserTestUtils.DATABASE_IMAGE;
import static com.lukrzak.ByForest.user.UserTestUtils.USERS_ENDPOINT_URI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class UserApiTests {

	private final User user = User.builder()
			.login("login")
			.email("email@em.com")
			.password("Password!123")
			.build();

	private final PostUserRequest newUserPostRequest = PostUserRequest.builder()
			.email("other@em.com")
			.login("other")
			.password("Password!123")
			.build();

	private final PostUserRequest invalidPasswordUserPostRequest = PostUserRequest.builder()
			.password("pass")
			.email("email@em.com")
			.login("otherLogin")
			.build();

	private final AuthenticationRequest correctAuthenticationRequest = AuthenticationRequest.builder()
			.email(user.getEmail())
			.password(user.getPassword())
			.build();

	private final AuthenticationRequest incorrectEmailAuthenticationRequest = AuthenticationRequest.builder()
			.email("incorrect@em.com")
			.password(user.getPassword())
			.build();

	private final AuthenticationRequest incorrectPasswordAuthenticationRequest = AuthenticationRequest.builder()
			.email(user.getEmail())
			.password("Incorrect!123")
			.build();

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private WebTestClient webTestClient;

	@Container
	@ServiceConnection
	private final static MySQLContainer<?> database = new MySQLContainer<>(DockerImageName.parse(DATABASE_IMAGE))
			.withDatabaseName("users-test-database")
			.withUsername("admin")
			.withPassword("password");

	@BeforeEach
	void clean() {
		userRepository.deleteAll();
	}

	@Test
	void testAddingUser() {
		int AMOUNT = 3;
		List<PostUserRequest> requests = UserTestUtils.generatePostUserRequests(AMOUNT);

		for (PostUserRequest req: requests) {
			assertEquals("User " + req + " saved successfully", getSaveUserResponse(req));
		}
		assertEquals(AMOUNT, userRepository.findAll().size());
	}

	@Test
	void testAddingUserWithTakenCredentials() {
		String expectedErrorMessage = "User with login: "
				+ newUserPostRequest.getLogin()
				+ " or email: " + newUserPostRequest.getEmail()
				+ " already exists";

		assertEquals("User " + newUserPostRequest + " saved successfully", getSaveUserResponse(newUserPostRequest));
		assertEquals(expectedErrorMessage, getSaveUserResponse(newUserPostRequest));
		assertEquals(1, userRepository.findAll().size());
	}

	@Test
	void testSavingWithInvalidCredentials() {
		String response = getSaveUserResponse(invalidPasswordUserPostRequest);

		assertEquals("Password must contain at least 8 characters including special character", response);
	}

	@Test
	void testUserAuthentication() {
		userRepository.save(user);

		String token = getAuthenticationResponse(correctAuthenticationRequest);

		assertNotNull(token);
	}

	@Test
	void testUserAuthenticationWithIncorrectCredentials() {
		userRepository.save(user);

		assertEquals("User with email: " + incorrectEmailAuthenticationRequest.getEmail() + " does not exist", getAuthenticationResponse(incorrectEmailAuthenticationRequest));
		assertEquals("Incorrect password", getAuthenticationResponse(incorrectPasswordAuthenticationRequest));
	}

	private String getSaveUserResponse(PostUserRequest userRequest) {
		return webTestClient.post()
				.uri(USERS_ENDPOINT_URI)
				.bodyValue(userRequest)
				.exchange()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();
	}

	private String getAuthenticationResponse(AuthenticationRequest request) {
		return webTestClient.post()
				.uri(USERS_ENDPOINT_URI + "/authenticate")
				.bodyValue(request)
				.exchange()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();
	}

}
