package my.practice.user.service;

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

	@Transactional
	public UserVo create(UserCreateDto createDto) {
		boolean duplicatedEmail = userRepository.countByEmail(createDto.getEmail()) > 0;
		if (duplicatedEmail) {
			throw new RuntimeException("중복된 Email입니다.");
		}
		User user = userRepository.save(new User(createDto.getEmail(), createDto.getPassword()));
		return new UserVo(user);
	}

}
