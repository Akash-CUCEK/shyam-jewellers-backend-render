package com.shyam.common.jwt;

import com.shyam.entity.Users;
import com.shyam.repository.UsersRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service("normalUserDetailsService")
@RequiredArgsConstructor
public class NormalUserDetailsService implements UserDetailsService {

  private final UsersRepo usersRepo;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Users user =
        usersRepo
            .findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    return User.builder()
        .username(user.getEmail())
        .password("OTP_AUTH_USER")
        .authorities("USERS")
        .build();
  }
}
