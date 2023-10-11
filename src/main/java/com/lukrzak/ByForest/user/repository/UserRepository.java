package com.lukrzak.ByForest.user.repository;

import com.lukrzak.ByForest.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByLoginOrEmail(String login, String email);

}
