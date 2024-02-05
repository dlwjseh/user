package my.practice.user.service;

import my.practice.user.exception.NotFoundUserException;
import my.practice.user.vo.SecurityUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import my.practice.user.domain.User;
import my.practice.user.dto.UserCreateDto;
import my.practice.user.repository.UserRepository;
import my.practice.user.vo.UserVo;

/**
 * 유저 서비스
 */
@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public UserVo create(UserCreateDto createDto) {
		boolean duplicatedEmail = userRepository.countByEmail(createDto.getEmail()) > 0;
		if (duplicatedEmail) {
			throw new RuntimeException("중복된 Email입니다.");
		}
		String encodedPassword = passwordEncoder.encode(createDto.getPassword());
		User user = userRepository.save(new User(createDto.getEmail(), encodedPassword));
		return new UserVo(user);
	}

	@Transactional(readOnly = true)
	public SecurityUser findByUsername(String username) {
		return userRepository.findByEmail(username)
				.map(u -> new SecurityUser(u.getEmail(), u.getPassword(), new UserVo(u)))
				.orElseThrow(NotFoundUserException::new);
	}

}
