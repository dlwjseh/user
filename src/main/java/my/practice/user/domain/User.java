package my.practice.user.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import my.practice.user.enums.UserRole;
import my.practice.user.enums.UserRoleConverter;
import my.practice.user.enums.UserStatus;

/**
 * 유저
 */
@Getter
@Entity
@Table(name = "t_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false, length = 100)
	private String email;

	@Column(nullable = false, length = 200)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 15)
	private UserStatus status;

	@Convert(converter = UserRoleConverter.class)
	private Set<UserRole> roles = new HashSet<>();

	@Column(nullable = false)
	private LocalDateTime lastPasswordChangeDate;

	private LocalDateTime lastLoginDate;

	@Column(nullable = false)
	private LocalDateTime createdDate;

	public User(String email, String password) {
		this.email = email;
		this.password = password;
		this.status = UserStatus.ACTIVE;
		this.roles = Set.of(UserRole.CLIENT);
		LocalDateTime now = LocalDateTime.now();
		this.lastPasswordChangeDate = now;
		this.createdDate = now;
	}
}
