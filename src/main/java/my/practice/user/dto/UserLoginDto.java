package my.practice.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * 로그인용 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class UserLoginDto implements Serializable {
	private String username;
	private String password;
	private String rememberMe;

	public String getUsername() {
		return StringUtils.hasText(username) ? username.toUpperCase() : "";
	}

	public UserLoginDto(String username, String password, String rememberMe) {
		this.username = username;
		this.password = password;
		this.rememberMe = rememberMe;
	}
}
