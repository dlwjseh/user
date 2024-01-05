package my.practice.user.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.AttributeConverter;

/**
 * 유저권한 DB Converter
 */
public class UserRoleConverter implements AttributeConverter<Set<UserRole>, String> {
	private static final String delimiter = ",";

	@Override
	public String convertToDatabaseColumn(Set<UserRole> attribute) {
		return attribute.stream().map(Enum::name).collect(Collectors.joining(delimiter));
	}

	@Override
	public Set<UserRole> convertToEntityAttribute(String dbData) {
		return Arrays.stream(dbData.split(delimiter)).map(UserRole::valueOf).collect(Collectors.toSet());
	}
}
