package com.shyam.common.jwt;

import com.shyam.entity.AdminUsers;
import com.shyam.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service("adminUserDetailsService")
@RequiredArgsConstructor
public class AdminUserDetailsService implements UserDetailsService {

  private final AdminRepository adminUsersRepo;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    AdminUsers admin =
        adminUsersRepo
            .findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Admin not found: " + email));

    return User.builder()
        .username(admin.getEmail())
        .password(admin.getPassword())
        .authorities("ROLE_" + admin.getRole().name())
        .build();
  }
}
