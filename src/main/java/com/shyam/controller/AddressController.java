package com.shyam.controller;

import com.shyam.entity.Address;
import com.shyam.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<Address> createAddress(@RequestBody Address address) {
        Address savedAddress = addressService.save(address);
        return ResponseEntity.ok(savedAddress);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable Long id,
                                                 @RequestBody Address address) {
        address.setAddressId(id);
        Address updatedAddress = addressService.update(address);
        return ResponseEntity.ok(updatedAddress);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<Address>> getAllAddresses(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        Page<Address> addresses = addressService.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(addresses);
    }

    @PatchMapping("/{id}/default")
    public ResponseEntity<Address> setDefaultAddress(@PathVariable Long id,
                                                     @RequestParam Long userId) {
        // We need to set the address with the given id as default for the user
        // and unset any other default address for the user.
        // We'll need to implement this in the service.
        // For now, we'll leave it as a TODO.
        throw new UnsupportedOperationException("Not implemented");
    }
}