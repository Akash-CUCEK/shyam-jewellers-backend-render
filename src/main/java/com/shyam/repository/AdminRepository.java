package com.shyam.repository;

import com.shyam.common.constants.Role;
import com.shyam.entity.AdminUsers;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<AdminUsers, Long> {
  Optional<AdminUsers> findByEmail(String email);

  List<AdminUsers> findByRole(Role role);
}
