package com.lukrzak.ByForest.user;

import com.lukrzak.ByForest.user.dto.PostUserRequest;

import java.util.LinkedList;
import java.util.List;

import static com.lukrzak.ByForest.ByForestApplication.BASE_URL;

public class UserTestUtils {

	public final static String DATABASE_IMAGE = "mysql:8.1.0";

	public final static String USERS_ENDPOINT_URI = BASE_URL + "/users";

	public static List<PostUserRequest> generatePostUserRequests(int amount) {
		List<PostUserRequest> requests = new LinkedList<>();
		for (int i = 0; i < amount; i++){
			requests.add(new PostUserRequest("Login" + i, "Password!12" + i, i + "email@ema.co"));
		}

		return requests;
	}

}
