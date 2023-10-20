package com.lukrzak.ByForest.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetUserResponse {

	private String login;

	private String email;

	@Override
	public String toString(){
		return "AuthenticationRequest[" + login + ", " + email + "]";
	}

}
