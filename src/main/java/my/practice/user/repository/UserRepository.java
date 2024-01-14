package my.practice.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import my.practice.user.domain.User;

import java.util.Optional;

/**
 * 유저 Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	long countByEmail(String email);
}
