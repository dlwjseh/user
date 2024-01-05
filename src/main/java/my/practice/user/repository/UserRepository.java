package my.practice.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import my.practice.user.domain.User;

/**
 * 유저 Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	long countByEmail(String email);
}
