package com.lukrzak.ByForest.user.controller;

import com.lukrzak.ByForest.user.dto.GetUserResponse;
import com.lukrzak.ByForest.user.dto.PostUserRequest;
import org.springframework.http.ResponseEntity;

public interface UserController {

	ResponseEntity<GetUserResponse> findUser(long id);

	ResponseEntity<String> saveUser(PostUserRequest userRequest);

	ResponseEntity<String> deleteUser(long id);

}
