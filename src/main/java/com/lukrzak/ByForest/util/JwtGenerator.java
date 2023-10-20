package com.lukrzak.ByForest.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtGenerator {

	private final Environment environment;

	private final long JWT_EXPIRATION_TIME = 1000 * 60 * 60 * 24;

	public String generateJwtToken(String subject){
		log.info("Invoked generation of JWT for: " + subject);
		return Jwts.builder()
				.issuer("ByForest")
				.subject(subject)
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME))
				.signWith(getSigningKey())
				.compact();
	}

	private Key getSigningKey() {
		String secret = environment.getProperty("jwt.key");
		if (secret == null)
			throw new NullPointerException("Secret key is null");
		byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}

}
