package com.repairmatch.modules.user.api;

import com.repairmatch.modules.user.dto.AddressDto;
import com.repairmatch.modules.user.dto.CreateAddressRequest;
import com.repairmatch.modules.user.dto.CustomerProfileDto;
import com.repairmatch.modules.user.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/profile")
    public ResponseEntity<CustomerProfileDto> getProfile(Authentication authentication) {
        return ResponseEntity.ok(customerService.getCustomerProfile(authentication.getName()));
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDto>> getAddresses(Authentication authentication) {
        return ResponseEntity.ok(customerService.getCustomerAddresses(authentication.getName()));
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressDto> addAddress(
            Authentication authentication,
            @Valid @RequestBody CreateAddressRequest request) {
        AddressDto dto = customerService.addAddress(authentication.getName(), request);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PutMapping("/addresses/{id}/default")
    public ResponseEntity<Void> setDefaultAddress(
            Authentication authentication,
            @PathVariable String id) {
        customerService.setDefaultAddress(authentication.getName(), id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(
            Authentication authentication,
            @PathVariable String id) {
        customerService.deleteAddress(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
