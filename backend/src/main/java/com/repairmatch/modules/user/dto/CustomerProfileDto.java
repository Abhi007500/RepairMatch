package com.repairmatch.modules.user.dto;

import java.util.List;

public class CustomerProfileDto {
    private String id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private List<AddressDto> addresses;

    public CustomerProfileDto() {}

    public CustomerProfileDto(String id, String email, String fullName, String phoneNumber, List<AddressDto> addresses) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.addresses = addresses;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public List<AddressDto> getAddresses() { return addresses; }
    public void setAddresses(List<AddressDto> addresses) { this.addresses = addresses; }
}
