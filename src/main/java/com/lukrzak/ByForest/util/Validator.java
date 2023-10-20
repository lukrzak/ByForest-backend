package com.lukrzak.ByForest.util;

import com.lukrzak.ByForest.exception.ViolatedConstraintException;
import com.lukrzak.ByForest.user.dto.PostUserRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Validator {

	// TODO: change to not hardcoded constraints
	private final static String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}";

	private final static String EMAIL_REGEX = "[a-zA-Z0-9]{2,}[@]{1}[a-zA-Z0-9]{1,}[.]{1}[a-z]{2,5}";

	private final static int MAX_LOGIN_LENGTH = 20;

	private final static int MIN_LOGIN_LENGTH = 3;

	public static void validateUserToSave(PostUserRequest request) throws ViolatedConstraintException {
		if (!request.getPassword().matches(PASSWORD_REGEX))
			throw new ViolatedConstraintException("Password must contain at least 8 characters including special character");
		log.info("Password for {} is valid", request);
		if (!request.getEmail().matches(EMAIL_REGEX))
			throw new ViolatedConstraintException("Invalid email format in " + request.getEmail());
		log.info("Email for {} is valid", request);
		if (!inBoundaries(request.getLogin().length(), MIN_LOGIN_LENGTH, MAX_LOGIN_LENGTH))
			throw new ViolatedConstraintException("Login must be 3 to 20 characters long in " + request.getLogin());
		log.info("Login for {} is valid", request);
	}

	private static boolean inBoundaries(int currentValue, int minValue, int maxValue) {
		return currentValue >= minValue && currentValue <= maxValue;
	}

}
