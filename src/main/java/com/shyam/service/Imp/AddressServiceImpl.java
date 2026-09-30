package com.shyam.service.Imp;

import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.entity.Address;
import com.shyam.repository.AddressRepository;
import com.shyam.service.AddressService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {

  private final AddressRepository addressRepository;

  @Override
  @Transactional
  public Address save(Address address) {
    log.info("Processing request to save address");
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
    Address saved = addressRepository.save(address);
    log.info("Address saved successfully with id: {}", saved.getAddressId());
    return saved;
  }

  @Override
  public Address findById(Long id) {
    log.info("Processing request to find address by id: {}", id);
    Address address = addressRepository.findById(id).orElse(null);
    if (address == null) {
      log.warn("Address not found with id: {}", id);
    } else {
      log.info("Address found with id: {}", id);
    }
    return address;
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Address> findAll(Pageable pageable) {
    log.info(
        "Processing request to find all addresses with page: {} and size: {}",
        pageable.getPageNumber(),
        pageable.getPageSize());
    Page<Address> addresses = addressRepository.findAll(pageable);
    log.info("Retrieved {} addresses", addresses.getTotalElements());
    return addresses;
  }

  @Override
  @Transactional
  public void deleteById(Long id) {
    log.info("Processing request to delete address by id: {}", id);
    addressRepository.deleteById(id);
    log.info("Address deleted successfully with id: {}", id);
  }

  @Override
  @Transactional
  public Address update(Address address) {
    log.info("Processing request to update address with id: {}", address.getAddressId());
    Address existing =
        addressRepository
            .findById(address.getAddressId())
            .orElseThrow(
                () -> {
                  log.warn("Address not found with id: {}", address.getAddressId());
                  return new SYMException(
                      HttpStatus.NOT_FOUND,
                      SYMErrorType.GENERIC_EXCEPTION,
                      "ADDRESS_NOT_FOUND",
                      "Address not found",
                      "Address not found");
                });
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
    Address updated = addressRepository.save(existing);
    log.info("Address updated successfully with id: {}", updated.getAddressId());
    return updated;
  }

  @Override
  @Transactional
  public Address setDefaultAddress(Long addressId, Long userId) {
    log.info(
        "Processing request to set default address. AddressId: {}, UserId: {}", addressId, userId);
    // Get the address
    Address address =
        addressRepository
            .findById(addressId)
            .orElseThrow(
                () -> {
                  log.warn("Address not found with id: {}", addressId);
                  return new SYMException(
                      HttpStatus.NOT_FOUND,
                      SYMErrorType.GENERIC_EXCEPTION,
                      "ADDRESS_NOT_FOUND",
                      "Address not found",
                      "Address not found");
                });
    // Check that the address belongs to the user
    if (!address.getUser().getUserId().equals(userId)) {
      log.warn("Address with id: {} does not belong to user with id: {}", addressId, userId);
      throw new SYMException(
          HttpStatus.FORBIDDEN,
          SYMErrorType.VALIDATION_FAILED,
          "ADDRESS_ACCESS_DENIED",
          "Address does not belong to the user",
          "Address with id: " + addressId + " does not belong to user with id: " + userId);
    }

    // Unset the current default address for the user
    List<Address> defaultAddresses =
        addressRepository.findByUser_UserId(userId).stream()
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

    Address updatedAddress = addressRepository.save(address);
    log.info("Address id: {} set as default for user id: {}", addressId, userId);
    return updatedAddress;
  }
}
