package com.shyam.entity;

import com.shyam.common.constants.ServiceType;
import com.shyam.common.constants.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_home")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceHome {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "service_home_seq")
    @SequenceGenerator(name = "service_home_seq", sequenceName = "service_home_seq", allocationSize = 1)
    private Long serviceId;

    private String name;

    private String address;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    private String notes;

    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}