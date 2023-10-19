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

import static com.lukrzak.ByForest.ByForestApplication.BASE_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class UserApiTests {

	private final static String DATABASE_IMAGE = "mysql:8.1.0";
	private final static String URI = BASE_URL + "/users";

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
		PostUserRequest userRequest = new PostUserRequest("login", "Password123!", "email@em.com");
		PostUserRequest otherUser = new PostUserRequest("lloogg", "Password123!", "emaaaa@em.com");

		assertEquals("User " + userRequest + " saved successfully", getSaveUserResponse(userRequest));
		assertEquals("User " + otherUser + " saved successfully", getSaveUserResponse(otherUser));
		assertEquals(2, userRepository.findAll().size());
	}

	@Test
	void testAddingUserWithTakenCredentials() {
		PostUserRequest userRequest = new PostUserRequest("login", "pass", "email@em.com");
		PostUserRequest userWithTakenCredentials = new PostUserRequest("login", "ppp", "emaaaa@em.com");

		assertEquals("User " + userRequest + " saved successfully", getSaveUserResponse(userRequest));
		assertEquals("User with login: " + userWithTakenCredentials.getLogin()
				+ " or email: " + userWithTakenCredentials.getEmail()
				+ " already exists", getSaveUserResponse(userWithTakenCredentials));
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
