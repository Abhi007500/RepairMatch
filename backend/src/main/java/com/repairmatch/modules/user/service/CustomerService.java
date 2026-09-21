package com.repairmatch.modules.user.service;

import com.repairmatch.common.exception.BadRequestException;
import com.repairmatch.common.exception.ResourceNotFoundException;
import com.repairmatch.modules.user.domain.Address;
import com.repairmatch.modules.user.domain.User;
import com.repairmatch.modules.user.dto.AddressDto;
import com.repairmatch.modules.user.dto.CreateAddressRequest;
import com.repairmatch.modules.user.dto.CustomerProfileDto;
import com.repairmatch.modules.user.repository.AddressRepository;
import com.repairmatch.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public CustomerService(UserRepository userRepository, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional(readOnly = true)
    public CustomerProfileDto getCustomerProfile(String email) {
        User user = getUserByEmail(email);
        List<AddressDto> addresses = addressRepository.findByUserId(user.getId()).stream()
                .map(AddressDto::fromEntity)
                .collect(Collectors.toList());

        return new CustomerProfileDto(user.getId(), user.getEmail(), user.getFullName(), user.getPhoneNumber(), addresses);
    }

    @Transactional(readOnly = true)
    public List<AddressDto> getCustomerAddresses(String email) {
        User user = getUserByEmail(email);
        return addressRepository.findByUserId(user.getId()).stream()
                .map(AddressDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressDto addAddress(String email, CreateAddressRequest request) {
        User user = getUserByEmail(email);
        List<Address> existing = addressRepository.findByUserId(user.getId());

        boolean shouldBeDefault = request.isDefault() || existing.isEmpty();

        if (shouldBeDefault && !existing.isEmpty()) {
            existing.forEach(a -> a.setDefault(false));
            addressRepository.saveAll(existing);
        }

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setUser(user);
        address.setStreet(request.getStreet().trim());
        address.setCity(request.getCity().trim());
        address.setState(request.getState().trim());
        address.setPostalCode(request.getPostalCode().trim());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        address.setDefault(shouldBeDefault);

        Address saved = addressRepository.save(address);
        return AddressDto.fromEntity(saved);
    }

    @Transactional
    public void setDefaultAddress(String email, String addressId) {
        User user = getUserByEmail(email);
        List<Address> addresses = addressRepository.findByUserId(user.getId());

        boolean found = false;
        for (Address addr : addresses) {
            if (addr.getId().equals(addressId)) {
                addr.setDefault(true);
                found = true;
            } else {
                addr.setDefault(false);
            }
        }

        if (!found) {
            throw new ResourceNotFoundException("Address not found with id: " + addressId);
        }

        addressRepository.saveAll(addresses);
    }

    @Transactional
    public void deleteAddress(String email, String addressId) {
        User user = getUserByEmail(email);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You are not authorized to delete this address");
        }

        addressRepository.delete(address);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
