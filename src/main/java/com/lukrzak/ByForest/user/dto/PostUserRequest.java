package com.lukrzak.ByForest.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostUserRequest {

	private String login;
	private String password;
	private String email;

	@Override
	public String toString(){
		return "PostUserRequest[" + login + ", " + email + "]";
	}

}
