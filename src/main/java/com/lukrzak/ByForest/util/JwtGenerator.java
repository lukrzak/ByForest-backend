package com.lukrzak.ByForest.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtGenerator {

	private String SECRET_KEY;
	private final Environment environment;
	private final long JWT_EXPIRATION_TIME = 1000 * 60 * 60 * 24;

	@PostConstruct
	private void setKey(){
		SECRET_KEY = environment.getProperty("jwt.key");
	}

	public String generateJwtToken(String subject){
		return Jwts.builder()
				.issuer("ByForest")
				.subject(subject)
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME))
				.signWith(getSigningKey())
				.compact();
	}

	private Key getSigningKey() {
		byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}

}
