package com.lukrzak.ByForest.util;

import com.lukrzak.ByForest.exception.ViolatedConstraintException;
import com.lukrzak.ByForest.user.dto.PostUserRequest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidatorTests {

	private final String VALID_LOGIN = "login";

	private final String VALID_PASSWORD = "Password!123";

	private final String VALID_EMAIL = "email@em.com";

	private final PostUserRequest validPostUserRequest = new PostUserRequest(VALID_LOGIN, VALID_PASSWORD, VALID_EMAIL);

	private final List<String> invalidLogins = new ArrayList<>();

	private final List<String> invalidEmails = new ArrayList<>();

	private final List<String> invalidPasswords = new ArrayList<>();

	{
		invalidLogins.addAll(List.of("l", "lo", "LOGIN_WITH_TWENTY_CHA"));
		invalidLogins.add(null);
		invalidEmails.addAll(List.of("e@ema.em", "email.com", "email@ema", "email@em.abcdef", "ema@ema.c", "#!#!@em.com"));
		invalidEmails.add(null);
		invalidPasswords.addAll(List.of("passwordddd", "Password", "Password123", "123123123", "Pa$sw0r"));
		invalidPasswords.add(null);
	}

	@Test
	void testValidUserPostRequest() {
		assertDoesNotThrow(() -> Validator.validateUserToSave(validPostUserRequest));
	}

	@Test
	void testInvalidLogins() {
		invalidLogins.forEach(l -> {
			PostUserRequest tmpPostUserRequest = new PostUserRequest(l, VALID_PASSWORD, VALID_EMAIL);
			assertThrows(ViolatedConstraintException.class, () -> Validator.validateUserToSave(tmpPostUserRequest));
		});
		invalidPasswords.forEach(p -> {
			PostUserRequest tmpPostUserRequest = new PostUserRequest(VALID_LOGIN, p, VALID_EMAIL);
			assertThrows(ViolatedConstraintException.class, () -> Validator.validateUserToSave(tmpPostUserRequest));
		});
		invalidEmails.forEach(e -> {
			PostUserRequest tmpPostUserRequest = new PostUserRequest(VALID_LOGIN, VALID_PASSWORD, e);
			assertThrows(ViolatedConstraintException.class, () -> Validator.validateUserToSave(tmpPostUserRequest));
		});
	}

}
