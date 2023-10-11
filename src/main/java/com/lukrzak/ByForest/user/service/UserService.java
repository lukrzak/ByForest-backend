package com.lukrzak.ByForest.user.service;

import com.lukrzak.ByForest.user.model.User;

public interface UserService {

	User findUser(long id);

	void saveUser(User user);

	void deleteUser(long id);

}
