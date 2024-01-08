package my.practice.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;

import my.practice.user.domain.User;
import my.practice.user.dto.UserCreateDto;
import my.practice.user.enums.UserRole;
import my.practice.user.enums.UserStatus;
import my.practice.user.repository.UserRepository;
import my.practice.user.vo.UserVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	@InjectMocks
	private UserService userService;
	@Mock
	private UserRepository userRepository;
	@Mock
	private PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("유저 생성")
	@SuppressWarnings("DataFlowIssue")
	void create() {
		// given
		UserCreateDto createDto = new UserCreateDto();
		createDto.setEmail("dlwjseh3@gmail.com");
		createDto.setPassword("password");
		String encodedPassword = "encodedPassword";
		LocalDateTime startDateTime = LocalDateTime.now();
		User user = new User(createDto.getEmail(), encodedPassword);

		when(userRepository.countByEmail(createDto.getEmail())).thenReturn(0L);
		when(passwordEncoder.encode(createDto.getPassword())).thenReturn(encodedPassword);
		when(userRepository.save(assertArg(arg -> {
			if (Objects.equals(arg.getEmail(), createDto.getEmail())) {
				return;
			}
			throw new RuntimeException();
		}))).thenReturn(user);

		// when
		UserVo userVo = userService.create(createDto);

		// then
		assertThat(userVo.getEmail()).isEqualTo(createDto.getEmail());
		assertThat(user.getPassword()).isEqualTo(encodedPassword);
		assertThat(userVo.getStatus()).isEqualTo(UserStatus.ACTIVE);
		assertThat(userVo.getRoles()).containsExactlyInAnyOrder(UserRole.CLIENT);
		assertThat(userVo.getLastPasswordChangeDate()).isAfterOrEqualTo(startDateTime);
		assertThat(userVo.getCreatedDate()).isAfterOrEqualTo(startDateTime);
		assertThat(userVo.getLastLoginDate()).isNull();
	}
}