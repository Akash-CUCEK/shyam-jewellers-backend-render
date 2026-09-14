package com.shyam.repository;

import com.shyam.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository
        extends JpaRepository<Address, Long>,
                JpaSpecificationExecutor<Address> {

    List<Address> findByUser_UserId(Long userId);
}