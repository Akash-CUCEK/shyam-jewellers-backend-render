package com.shyam.entity;

import com.shyam.common.constants.Status;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "repair_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairService {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "repair_service_seq")
  @SequenceGenerator(
      name = "repair_service_seq",
      sequenceName = "repair_service_seq",
      allocationSize = 1)
  private Long serviceId;

  private String name;

  private String address;

  @Enumerated(EnumType.STRING)
  private Status status;

  @Column(name = "mobile_number")
  private String mobileNumber;

  private String notes;

  private String createdBy;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  private String updatedBy;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
