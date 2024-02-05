package my.practice.user.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import my.practice.user.dto.UserCreateDto;
import my.practice.user.security.JwtTokenProvider;
import my.practice.user.service.UserService;
import my.practice.user.vo.SecurityUser;
import my.practice.user.vo.UserVo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 유저 API
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserApi {
	private final UserService userService;
	private final JwtTokenProvider jwtTokenProvider;

	/**
	 * 유저 등록
	 */
	@PostMapping
	public ResponseEntity<UserVo> createUser(@RequestBody UserCreateDto createDto) {
		return ResponseEntity.ok(userService.create(createDto));
	}

	/**
	 * Refresh Token으로 Token 재발급
	 */
	@GetMapping("/refresh")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String refreshToken = request.getHeader(JwtTokenProvider.REFRESH_HEADER_NAME);
		jwtTokenProvider.validateToken(refreshToken);
		String username = jwtTokenProvider.getUsernameFromRefreshToken(refreshToken);
		SecurityUser user = userService.findByUsername(username);
		jwtTokenProvider.issueToken(response, user);
	}

}
