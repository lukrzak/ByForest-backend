package com.lukrzak.ByForest.user;

import com.lukrzak.ByForest.user.dto.PostUserRequest;
import com.lukrzak.ByForest.user.mapper.UserMapper;
import com.lukrzak.ByForest.user.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserMapperTests {

	@Test
	void testMappingFromPostUserRequestToUser(){
		PostUserRequest req = new PostUserRequest("test", "t3sT", "test@email.com");

		User mappedUser = UserMapper.mapToUser(req);

		assertEquals(mappedUser.getLogin(), req.getLogin());
		assertEquals(mappedUser.getEmail(), req.getEmail());
		assertEquals(mappedUser.getPassword(), req.getPassword());
		assertNull(mappedUser.getId());
	}

}
