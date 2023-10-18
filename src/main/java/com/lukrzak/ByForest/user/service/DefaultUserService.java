package com.lukrzak.ByForest.user.service;

import com.lukrzak.ByForest.user.dto.GetUserResponse;
import com.lukrzak.ByForest.user.dto.PostUserRequest;
import com.lukrzak.ByForest.exception.CredentialsAlreadyTakenException;
import com.lukrzak.ByForest.exception.UserDoesntExistException;
import com.lukrzak.ByForest.user.mapper.UserMapper;
import com.lukrzak.ByForest.user.model.User;
import com.lukrzak.ByForest.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultUserService implements UserService {

	private final UserRepository userRepository;

	@Override
	public GetUserResponse findUser(long id) throws UserDoesntExistException {
		User foundUser = userRepository.findById(id)
				.orElseThrow(() -> new UserDoesntExistException("User with id: " + id + " does not exist"));
		log.info("User with id: {} has been found: {}-{}", id, foundUser.getLogin(), foundUser.getEmail());
		return UserMapper.mapToGetUserResponse(foundUser);
	}

	@Override
	public void saveUser(PostUserRequest user) throws CredentialsAlreadyTakenException {
		checkIfLoginOrEmailTaken(user);
		User userToSave = UserMapper.mapToUser(user);
		log.info("New user entity has been created - id: {}, login: {}, email: {}",
				userToSave.getId(), userToSave.getLogin(), userToSave.getEmail());
		userRepository.save(userToSave);
		log.info("User has been saved to database");
	}

	@Override
	public void deleteUser(long id) {
		userRepository.deleteById(id);
		log.info("User with id: {} has been deleted", id);
	}

	private void checkIfLoginOrEmailTaken(PostUserRequest userRequest) throws CredentialsAlreadyTakenException {
		Optional<User> user = userRepository.findByLoginOrEmail(userRequest.getLogin(), userRequest.getEmail());
		if (user.isPresent()){
			String message = String.format("User with login: %s or email: %s already exists", userRequest.getLogin(), userRequest.getEmail());
			throw new CredentialsAlreadyTakenException(message);
		}
		log.info("login: {} and email: {} are not taken", userRequest.getLogin(), userRequest.getPassword());
	}

}
