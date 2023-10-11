package com.lukrzak.ByForest.user.controller;

import com.lukrzak.ByForest.user.model.User;

public interface UserController {

	User findUser(long id);

	void saveUser(User user);

	void deleteUser(long id);

}
