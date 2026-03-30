package com.shyam.entity;

import com.shyam.common.constants.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUsers {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "admin_users_seq")
    @SequenceGenerator(name = "admin_users_seq", sequenceName = "admin_users_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;
    private String otp;

    @Column(name = "otp_generated_time")
    private LocalDateTime otpGeneratedTime;

    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Role role;
}