package my.practice.user.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import my.practice.user.dto.UserCreateDto;
import my.practice.user.service.UserService;
import my.practice.user.vo.UserVo;

/**
 * 유저 API
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserApi {
	private final UserService userService;

	/**
	 * 유저 등록
	 */
	@PostMapping
	public ResponseEntity<UserVo> createUser(@RequestBody UserCreateDto createDto) {
		return ResponseEntity.ok(userService.create(createDto));
	}

}
