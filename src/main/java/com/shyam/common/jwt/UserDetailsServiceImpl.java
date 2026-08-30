package com.shyam.common.jwt;

import com.shyam.entity.AdminUsers;
import com.shyam.entity.Users;
import com.shyam.repository.AdminRepository;
import com.shyam.repository.UsersRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl {

    private final AdminRepository adminRepository;
    private final UsersRepo usersRepo;

    public UserDetails loadUserByUsername(String username, String role)
            throws UsernameNotFoundException {

        if (role == null || role.isBlank()) {
            throw new UsernameNotFoundException("Role missing in JWT");
        }

        return switch (role.toUpperCase()) {

            // =====================================================
            // ADMIN
            // =====================================================

            case "ADMIN", "SUPER_ADMIN" -> {

                AdminUsers admin =
                        adminRepository
                                .findByEmail(username)
                                .orElseThrow(
                                        () ->
                                                new UsernameNotFoundException(
                                                        "Admin not found: " + username));

                yield User.builder()
                        .username(admin.getEmail())
                        .password("OTP_AUTH_ADMIN") // Placeholder password for OTP-based auth
                        .authorities("ROLE_" + admin.getRole().name())
                        .build();
            }

            // =====================================================
            // NORMAL USER
            // =====================================================

            case "USER" -> {

                Users user =
                        usersRepo
                                .findByEmail(username)
                                .orElseThrow(
                                        () ->
                                                new UsernameNotFoundException(
                                                        "User not found: " + username));

                yield User.builder()
                        .username(user.getEmail())
                        .password("OTP_AUTH_USER")
                        .authorities("ROLE_USER")
                        .build();
            }

            // =====================================================
            // INVALID ROLE
            // =====================================================

            default ->
                    throw new UsernameNotFoundException(
                            "Invalid role in JWT: " + role);
        };
    }
}