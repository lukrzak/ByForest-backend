package com.lukrzak.ByForest.user.service;

import com.lukrzak.ByForest.exception.ViolatedConstraintException;
import com.lukrzak.ByForest.user.dto.AuthenticationRequest;
import com.lukrzak.ByForest.user.dto.GetUserResponse;
import com.lukrzak.ByForest.user.dto.PostUserRequest;
import com.lukrzak.ByForest.exception.CredentialsAlreadyTakenException;
import com.lukrzak.ByForest.exception.UserDoesntExistException;
import com.lukrzak.ByForest.user.mapper.UserMapper;
import com.lukrzak.ByForest.user.model.User;
import com.lukrzak.ByForest.user.repository.UserRepository;
import com.lukrzak.ByForest.util.JwtGenerator;
import com.lukrzak.ByForest.util.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultUserService implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder encoder;
	private final JwtGenerator jwtGenerator;

	@Override
	public User saveUser(PostUserRequest user) throws CredentialsAlreadyTakenException, ViolatedConstraintException {
		Validator.validateUserToSave(user);
		checkIfLoginOrEmailTaken(user);
		User userToSave = createUserEntityFromDto(user);
		userRepository.save(userToSave);
		log.info("{} has been saved to the database", user);

		return userToSave;
	}

	@Override
	public String authenticateUser(AuthenticationRequest authenticationRequest) throws UserDoesntExistException {
		User user = userRepository.findByEmail(authenticationRequest.getEmail())
				.orElseThrow(() -> new UserDoesntExistException("User does not exist"));
		if (!encoder.matches(authenticationRequest.getPassword(), user.getPassword()))
			throw new UserDoesntExistException("Incorrect password");

		return jwtGenerator.generateJwtToken(user.getEmail());
	}

	@Override
	public GetUserResponse findUser(long id) throws UserDoesntExistException {
		User foundUser = userRepository.findById(id)
				.orElseThrow(() -> new UserDoesntExistException("User with id: " + id + " does not exist"));
		log.info("User with id: {} has been found: {}-{}", id, foundUser.getLogin(), foundUser.getEmail());
		return UserMapper.mapToGetUserResponse(foundUser);
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

	private User createUserEntityFromDto(PostUserRequest postUserRequest) {
		User userToSave = UserMapper.mapToUser(postUserRequest);
		String encodedPassword = encoder.encode(postUserRequest.getPassword());
		log.info("Encoded password: {}", encodedPassword);
		userToSave.setPassword(encodedPassword);
		log.info("New user entity has been created: {}", userToSave);

		return userToSave;
	}

}
