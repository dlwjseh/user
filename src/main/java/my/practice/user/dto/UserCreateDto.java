package my.practice.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 유저 생성용 DTO
 */
@Getter
@Setter
@ToString
public class UserCreateDto {
	private String email;
	private String password;
}
