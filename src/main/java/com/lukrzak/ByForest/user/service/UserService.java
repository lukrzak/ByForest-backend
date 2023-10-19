package com.lukrzak.ByForest.user.service;

import com.lukrzak.ByForest.user.dto.GetUserResponse;
import com.lukrzak.ByForest.user.dto.PostUserRequest;
import com.lukrzak.ByForest.exception.CredentialsAlreadyTakenException;
import com.lukrzak.ByForest.exception.UserDoesntExistException;
import com.lukrzak.ByForest.user.model.User;

public interface UserService {

	GetUserResponse findUser(long id) throws UserDoesntExistException;

	User saveUser(PostUserRequest user) throws CredentialsAlreadyTakenException;

	void deleteUser(long id);

}
