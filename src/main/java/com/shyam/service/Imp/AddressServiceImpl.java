package com.shyam.service.Imp;

import com.shyam.entity.Address;
import com.shyam.entity.Users;
import com.shyam.repository.AddressRepository;
import com.shyam.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public Address save(Address address) {
        // Set audit fields for new entity
        if (address.getCreatedAt() == null) {
            address.setCreatedAt(LocalDateTime.now());
        }
        if (address.getCreatedBy() == null) {
            // In a real application, we would get the current user from the security context
            // For now, we'll leave it as null or set a default? We'll follow the existing pattern.
            // Looking at ProductServiceImpl, it sets createdBy from the requestDTO.
            // We'll assume the address object passed in already has the createdBy set by the controller.
            // If not, we can set it to a default or leave it null and let the database handle it?
            // But the column is nullable? We'll leave it as is and assume the controller sets it.
        }
        return addressRepository.save(address);
    }

    @Override
    public Address findById(Long id) {
        return addressRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Address> findAll(Pageable pageable) {
        return addressRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        addressRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Address update(Address address) {
        Address existing = addressRepository.findById(address.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));
        // Update fields
        existing.setAddressLabel(address.getAddressLabel());
        existing.setAddressLine1(address.getAddressLine1());
        existing.setAddressLine2(address.getAddressLine2());
        existing.setCity(address.getCity());
        existing.setState(address.getState());
        existing.setPincode(address.getPincode());
        existing.setPhoneNumber(address.getPhoneNumber());
        existing.setIsDefault(address.getIsDefault());
        // Set updated audit fields
        existing.setUpdatedAt(LocalDateTime.now());
        // updatedBy should be set by the controller from the request
        return addressRepository.save(existing);
    }

    @Override
    @Transactional
    public Address setDefaultAddress(Long addressId, Long userId) {
        // Get the address
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        // Check that the address belongs to the user
        if (!address.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Address does not belong to the user");
        }

        // Unset the current default address for the user
        List<Address> defaultAddresses = addressRepository.findByUser_UserId(userId)
                .stream()
                .filter(Addr -> Addr.getIsDefault() != null && Addr.getIsDefault())
                .toList();
        for (Address defaultAddr : defaultAddresses) {
            defaultAddr.setIsDefault(false);
            addressRepository.save(defaultAddr);
        }

        // Set the given address as default
        address.setIsDefault(true);
        address.setUpdatedAt(LocalDateTime.now());
        address.setUpdatedBy(address.getCreatedBy()); // Set updatedBy to createdBy for now

        return addressRepository.save(address);
    }
}