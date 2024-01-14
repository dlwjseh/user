package my.practice.user.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import my.practice.user.domain.User;
import my.practice.user.enums.UserRole;
import my.practice.user.enums.UserStatus;

/**
 * 유저 VO
 */
@Getter
@NoArgsConstructor
public class UserVo implements Serializable {

	private Long id;

	private String email;

	private UserStatus status;

	private Set<UserRole> roles;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime lastPasswordChangeDate;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime lastLoginDate;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdDate;

	public UserVo(User entity) {
		this.id = entity.getId();
		this.email = entity.getEmail();
		this.status = entity.getStatus();
		this.roles = entity.getRoles();
		this.lastPasswordChangeDate = entity.getLastPasswordChangeDate();
		this.lastLoginDate = entity.getLastLoginDate();
		this.createdDate = entity.getCreatedDate();
	}
}
