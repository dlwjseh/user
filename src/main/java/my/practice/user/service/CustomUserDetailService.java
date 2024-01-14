package my.practice.user.service;

import lombok.RequiredArgsConstructor;
import my.practice.user.repository.UserRepository;
import my.practice.user.vo.SecurityUser;
import my.practice.user.vo.UserVo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 로그인 시 유저 조회 서비스
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(u -> new SecurityUser(u.getEmail(), u.getPassword(), new UserVo(u)))
                .orElse(null);
    }
}
