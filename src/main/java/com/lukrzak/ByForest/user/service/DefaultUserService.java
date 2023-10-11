package com.lukrzak.ByForest.user.service;

import com.lukrzak.ByForest.user.model.User;
import com.lukrzak.ByForest.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

	private final UserRepository userRepository;

	@Override
	public User findUser(long id) {
		return null;
	}

	@Override
	public void saveUser(User user) {

	}

	@Override
	public void deleteUser(long id) {

	}

}
