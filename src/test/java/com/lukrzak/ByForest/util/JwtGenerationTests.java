package com.lukrzak.ByForest.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwtGenerationTests {

	private static JwtGenerator generator;

	@BeforeAll
	static void setup() {
		Environment environment = mock(Environment.class);
		generator = new JwtGenerator(environment);

		when(environment.getProperty(eq("jwt.key"))).thenReturn("AAAABBBBCCCCDDDDEEEEFFFFGGGGHHHH");
	}

	@Test
	void testTokenGeneration() {
		assertNotNull(generator.generateJwtToken("subject"));
	}
}
