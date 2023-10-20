package com.lukrzak.ByForest.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AuthenticationRequest {

	private String email;

	private String password;

	@Override
	public String toString(){
		return "AuthenticationRequest[" + email + "]";
	}

}
