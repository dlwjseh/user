package my.practice.user.vo;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.HashSet;

public class SecurityUser extends User {
    private final UserVo userVo;
    public SecurityUser(String username, String password, UserVo userVo) {
        super(username, password, new HashSet<>());
        this.userVo = userVo;
        this.getAuthorities().addAll(
                userVo.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.name())).toList()
        );
    }

    public UserVo getUserVo() {
        return userVo;
    }
}
