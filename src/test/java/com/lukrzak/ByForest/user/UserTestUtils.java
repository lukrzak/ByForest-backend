package com.lukrzak.ByForest.user;

import com.lukrzak.ByForest.user.dto.AuthenticationRequest;
import com.lukrzak.ByForest.user.dto.GetUserResponse;
import com.lukrzak.ByForest.user.dto.PostUserRequest;
import com.lukrzak.ByForest.user.model.User;

import java.util.LinkedList;
import java.util.List;

import static com.lukrzak.ByForest.ByForestApplication.BASE_URL;

public class UserTestUtils {

	public final static String DATABASE_IMAGE = "mysql:8.1.0";
	public final static String URI = BASE_URL + "/users";
	private final static User dummyUser = new User(1L, "login", "Password!123", "email@em.com");

	public static User getDummyUser() {
		return dummyUser;
	}

	public static PostUserRequest getExistingUserPostRequest() {
		return PostUserRequest.builder()
						.login(dummyUser.getLogin())
						.email(dummyUser.getEmail())
						.password(dummyUser.getPassword())
						.build();
	}

	public static PostUserRequest getNewUserPostRequest() {
		return PostUserRequest.builder()
				.login("newLogin")
				.password("Password!123")
				.email("ema@em.com")
				.build();
	}

	public static List<PostUserRequest> generatePostUserRequests(int amount) {
		List<PostUserRequest> requests = new LinkedList<>();
		for (int i = 0; i < amount; i++){
			requests.add(new PostUserRequest("Login" + i, "Password!12" + i, i + "email@ema.co"));
		}

		return requests;
	}

	public static AuthenticationRequest getExistingUserAuthenticationRequest() {
		return AuthenticationRequest.builder()
				.email(dummyUser.getEmail())
				.password(dummyUser.getPassword())
				.build();
	}

	public static AuthenticationRequest getIncorrectEmailUserAuthenticationRequest() {
		return AuthenticationRequest.builder()
				.email("incorrect@email.com")
				.password(dummyUser.getPassword())
				.build();
	}

	public static AuthenticationRequest getIncorrectPasswordUserAuthenticationRequest() {
		return AuthenticationRequest.builder()
						.email(dummyUser.getEmail())
						.password("Incorrect!23")
						.build();
	}

	public static GetUserResponse getGetUserResponse() {
		return GetUserResponse.builder()
				.email(dummyUser.getEmail())
				.login(dummyUser.getLogin())
				.build();
	}

}
