package my.practice.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.Serializable;
import java.util.HashSet;

/**
 * 로그인용 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class UserLoginDto implements Serializable {
	private String username;
	private String password;

	public UserLoginDto(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public UsernamePasswordAuthenticationToken createAuthRequest() {
		return new UsernamePasswordAuthenticationToken(this.username, this.password, new HashSet<>());
	}
}
