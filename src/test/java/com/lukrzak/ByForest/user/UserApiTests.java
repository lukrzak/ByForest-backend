package com.lukrzak.ByForest.user;

import com.lukrzak.ByForest.user.controller.DefaultUserController;
import com.lukrzak.ByForest.user.dto.PostUserRequest;
import com.lukrzak.ByForest.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

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
			.withDatabaseName("forest-test-database")
			.withUsername("admin")
			.withPassword("password");

	@AfterEach
	void clean(){
		userRepository.deleteAll();
	}

	@Test
	void testAddingUser(){
		PostUserRequest userRequest = new PostUserRequest("login", "pass", "email@em.com");
		PostUserRequest otherUser = new PostUserRequest("ll", "ppp", "em@em.com");
		PostUserRequest userWithTakenCredentials = new PostUserRequest("login", "ppp", "emaaa@em.com");

		assertEquals("User " + userRequest + " saved successfully", getPostUserResponse(userRequest));
		assertEquals("User " + otherUser + " saved successfully", getPostUserResponse(otherUser));
		assertEquals("User with login: " + userWithTakenCredentials.getLogin()
				+ " or email: " + userWithTakenCredentials.getEmail()
				+ " already exists", getPostUserResponse(userWithTakenCredentials));
	}

	private String getPostUserResponse(PostUserRequest userRequest){
		return webTestClient.post()
				.uri(URI)
				.bodyValue(userRequest)
				.exchange()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();
	}

}
