package com.lukrzak.ByForest.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtGenerator {

	@Value("${jwt.key}")
	private String SECRET_KEY;
	private final long JWT_EXPIRATION_TIME = 1000 * 60 * 60 * 24;

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
