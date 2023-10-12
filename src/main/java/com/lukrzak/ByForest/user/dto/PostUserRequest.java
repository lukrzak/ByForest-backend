package com.lukrzak.ByForest.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostUserRequest {

	private String login;
	private String password;
	private String email;

}
