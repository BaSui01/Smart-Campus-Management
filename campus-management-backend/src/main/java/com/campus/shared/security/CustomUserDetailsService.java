package com.campus.shared.security;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.domain.entity.Role;
import com.campus.domain.entity.User;
import com.campus.domain.entity.UserRole;
import com.campus.domain.repository.UserRepository;

/**
 * 自定义用户详情服务
 * 专门用于Spring Security认证
 *
 * @author campus
 * @since 2025-06-07
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameAndStatus(username, 1)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getStatus() == 1,
                true,
                true,
                true,
                getAuthorities(user)
        );
    }

    /**
     * 获取用户权限
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return user.getUserRoles().stream()
                .map(UserRole::getRole)
                .map(Role::getRoleKey)
                .map(roleKey -> new SimpleGrantedAuthority("ROLE_" + roleKey))
                .collect(Collectors.toList());
    }
}
