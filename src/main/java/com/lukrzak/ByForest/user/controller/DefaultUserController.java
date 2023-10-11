package com.lukrzak.ByForest.user.controller;

import com.lukrzak.ByForest.user.model.User;
import com.lukrzak.ByForest.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.lukrzak.ByForest.ByForestApplication.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/users")
@RequiredArgsConstructor
public class DefaultUserController implements UserController {

	private final UserService userService;

	@Override
	public User findUser(long id) {
		return null;
	}

	@Override
	public void saveUser(User user) {

	}

	@Override
	public void deleteUser(long id) {

	}
}
