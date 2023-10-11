package com.lukrzak.ByForest.user.dto;

import lombok.Getter;

@Getter
public class PostUserRequest {

	private String login;
	private String password;
	private String email;

}
