package com.lukrzak.ByForest.user.mapper;

import com.lukrzak.ByForest.user.dto.GetUserResponse;
import com.lukrzak.ByForest.user.dto.PostUserRequest;
import com.lukrzak.ByForest.user.model.User;

public interface UserMapper {

	static GetUserResponse mapToGetUserResponse(User user) {
		return GetUserResponse.builder()
				.login(user.getLogin())
				.email(user.getEmail())
				.build();
	}

	static User mapToUser(PostUserRequest postUserRequest) {
		return User.builder()
				.email(postUserRequest.getEmail())
				.login(postUserRequest.getLogin())
				.password(postUserRequest.getPassword())
				.build();
	}

}
