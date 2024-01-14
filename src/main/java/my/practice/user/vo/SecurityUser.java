package my.practice.user.vo;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.stream.Collectors;

public class SecurityUser extends User {
    private final UserVo userVo;
    public SecurityUser(String username, String password, UserVo userVo) {
        super(username, password,
                userVo.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.name())).collect(Collectors.toSet()));
        this.userVo = userVo;
    }

    public UserVo getUserVo() {
        return userVo;
    }
}
