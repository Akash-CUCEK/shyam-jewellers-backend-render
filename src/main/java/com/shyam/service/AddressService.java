package com.shyam.service;

import com.shyam.entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AddressService {

    Address save(Address address);

    Address findById(Long id);

    Page<Address> findAll(Pageable pageable);

    void deleteById(Long id);

    Address update(Address address);

    Address setDefaultAddress(Long addressId, Long userId);
}