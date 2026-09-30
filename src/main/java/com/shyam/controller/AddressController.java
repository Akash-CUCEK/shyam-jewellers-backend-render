package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.entity.Address;
import com.shyam.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Address", description = "Address management endpoints")
public class AddressController {

  private final AddressService addressService;

  @Operation(summary = "Create a new address", description = "Create a new address for a user.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Address created successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PostMapping
  public BaseResponseDTO<Address> createAddress(@Valid @RequestBody Address address) {
    log.info("Entering createAddress method with address: {}", address);
    Address savedAddress = addressService.save(address);
    log.info("Address created successfully with id: {}", savedAddress.getAddressId());
    return new BaseResponseDTO<>(savedAddress, null);
  }

  @Operation(
      summary = "Update an existing address",
      description = "Update an existing address by its ID.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Address updated successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "404", description = "Address not found", content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PutMapping("/{id}")
  public BaseResponseDTO<Address> updateAddress(
      @PathVariable Long id, @Valid @RequestBody Address address) {
    log.info("Entering updateAddress method with id: {} and address: {}", id, address);
    address.setAddressId(id);
    Address updatedAddress = addressService.update(address);
    log.info("Address updated successfully with id: {}", updatedAddress.getAddressId());
    log.info("Exiting updateAddress method");
    return new BaseResponseDTO<>(updatedAddress, null);
  }

  @Operation(summary = "Delete an address", description = "Delete an address by its ID.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Address deleted successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "404", description = "Address not found", content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @DeleteMapping("/{id}")
  public BaseResponseDTO<Void> deleteAddress(@PathVariable Long id) {
    log.info("Entering deleteAddress method with id: {}", id);
    addressService.deleteById(id);
    log.info("Address deleted successfully with id: {}", id);
    log.info("Exiting deleteAddress method");
    return new BaseResponseDTO<>(null, null);
  }

  @Operation(
      summary = "Get all addresses",
      description = "Retrieve a paginated list of all addresses.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful retrieval",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @GetMapping
  public BaseResponseDTO<Page<Address>> getAllAddresses(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    log.info("Entering getAllAddresses method with page: {} and size: {}", page, size);
    Pageable pageable = PageRequest.of(page, size);
    Page<Address> addresses = addressService.findAll(pageable);
    log.info("Successfully retrieved addresses, page: {}, size: {}", page, size);
    log.info("Exiting getAllAddresses method");
    return new BaseResponseDTO<>(addresses, null);
  }

  @Operation(
      summary = "Set default address",
      description = "Set an address as the default address for a user.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Default address set successfully",
        content = @Content(schema = @Schema(implementation = BaseResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - insufficient role",
        content = @Content),
    @ApiResponse(
        responseCode = "404",
        description = "Address or user not found",
        content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
  })
  @PatchMapping("/{id}/default")
  public BaseResponseDTO<Address> setDefaultAddress(
      @PathVariable Long id, @RequestParam Long userId) {
    log.info("Entering setDefaultAddress method with id: {} and userId: {}", id, userId);
    // TODO: Implement this in the service when ready
    // For now, returning a placeholder response
    Address address = addressService.findById(id);
    if (address == null) {
      log.error("Address not found with id: {}", id);
      throw new IllegalArgumentException("Address not found with id: " + id);
    }
    // In a real implementation, we would:
    // 1. Unset any existing default address for the user
    // 2. Set this address as default for the user
    log.info("Address id: {} set as default for user id: {}", id, userId);
    log.info("Exiting setDefaultAddress method");
    return new BaseResponseDTO<>(address, null);
  }
}
