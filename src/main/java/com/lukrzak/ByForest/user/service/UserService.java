package com.lukrzak.ByForest.user.service;

import com.lukrzak.ByForest.exception.ViolatedConstraintException;
import com.lukrzak.ByForest.user.dto.AuthenticationRequest;
import com.lukrzak.ByForest.user.dto.PostUserRequest;
import com.lukrzak.ByForest.exception.CredentialsAlreadyTakenException;
import com.lukrzak.ByForest.exception.UserException;
import com.lukrzak.ByForest.user.model.User;

public interface UserService {

	User saveUser(PostUserRequest user) throws CredentialsAlreadyTakenException, ViolatedConstraintException;

	String authenticateUser(AuthenticationRequest authenticationRequest) throws UserException;

}
