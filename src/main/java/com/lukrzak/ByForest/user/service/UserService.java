package com.lukrzak.ByForest.user.service;

import com.lukrzak.ByForest.user.dto.GetUserResponse;
import com.lukrzak.ByForest.user.dto.PostUserRequest;
import com.lukrzak.ByForest.user.exception.CredentialsAlreadyTakenException;
import com.lukrzak.ByForest.user.exception.UserDoesntExistException;

public interface UserService {

	GetUserResponse findUser(long id) throws UserDoesntExistException;

	void saveUser(PostUserRequest user) throws CredentialsAlreadyTakenException;

	void deleteUser(long id);

}
