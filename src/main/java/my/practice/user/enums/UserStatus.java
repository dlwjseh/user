package my.practice.user.enums;

/**
 * 유저 상태
 */
public enum UserStatus {
	ACTIVE("사용"),
	DORMANCY("휴면"),
	CLOSED("탈퇴"),
	LOCKED("비밀번호 5회 틀림")
	;

	public final String description;

	UserStatus(String description) {
		this.description = description;
	}
}
