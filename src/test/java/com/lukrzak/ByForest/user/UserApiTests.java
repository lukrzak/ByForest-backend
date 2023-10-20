package com.lukrzak.ByForest.user;

import com.lukrzak.ByForest.user.dto.PostUserRequest;
import com.lukrzak.ByForest.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
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
import static com.lukrzak.ByForest.user.UserTestUtils.URI;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class UserApiTests {

	private static final PostUserRequest newUserPostRequest = UserTestUtils.getNewUserPostRequest();
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

	@AfterEach
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
		PostUserRequest userWithTakenCredentials = new PostUserRequest(newUserPostRequest.getLogin(), "Password!123", "emaaaa@em.com");
		String expectedErrorMessage = "User with login: "
				+ userWithTakenCredentials.getLogin()
				+ " or email: " + userWithTakenCredentials.getEmail()
				+ " already exists";

		assertEquals("User " + newUserPostRequest + " saved successfully", getSaveUserResponse(newUserPostRequest));
		assertEquals(expectedErrorMessage, getSaveUserResponse(userWithTakenCredentials));
		assertEquals(1, userRepository.findAll().size());
	}

	@Test
	void testAddingUserWithPasswordHashing() {
		// TODO
	}

	@Test
	void testAddingUserWithIncorrectCredentials() {
		// TODO
	}

	private String getSaveUserResponse(PostUserRequest userRequest) {
		return webTestClient.post()
				.uri(URI)
				.bodyValue(userRequest)
				.exchange()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();
	}

}
