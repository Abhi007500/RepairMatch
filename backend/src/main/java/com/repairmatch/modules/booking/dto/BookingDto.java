package com.repairmatch.modules.booking.dto;

import com.repairmatch.modules.booking.domain.Booking;
import com.repairmatch.modules.user.dto.AddressDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BookingDto {

    private String id;
    private String bookingReference;
    private String customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String technicianId;
    private String technicianName;
    private String technicianPhone;
    private String categoryId;
    private String categoryName;
    private String brandId;
    private String brandName;
    private String modelId;
    private String modelName;
    private String problemTypeId;
    private String problemTypeTitle;
    private String problemDescription;
    private AddressDto address;
    private String scheduledDate;
    private String timeSlot;
    private String status;
    private BigDecimal inspectionFee;
    private BigDecimal finalAmount;
    private String paymentStatus;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<BookingTimelineDto> timeline;

    public BookingDto() {}

    public static BookingDto fromEntity(Booking b) {
        BookingDto dto = new BookingDto();
        dto.id = b.getId();
        dto.bookingReference = b.getBookingReference();
        if (b.getCustomer() != null) {
            dto.customerId = b.getCustomer().getId();
            dto.customerName = b.getCustomer().getFullName();
            dto.customerEmail = b.getCustomer().getEmail();
            dto.customerPhone = b.getCustomer().getPhoneNumber();
        }
        if (b.getTechnician() != null) {
            dto.technicianId = b.getTechnician().getId();
            if (b.getTechnician().getUser() != null) {
                dto.technicianName = b.getTechnician().getUser().getFullName();
                dto.technicianPhone = b.getTechnician().getUser().getPhoneNumber();
            }
        }
        if (b.getCategory() != null) {
            dto.categoryId = b.getCategory().getId();
            dto.categoryName = b.getCategory().getName();
        }
        if (b.getBrand() != null) {
            dto.brandId = b.getBrand().getId();
            dto.brandName = b.getBrand().getName();
        }
        if (b.getModel() != null) {
            dto.modelId = b.getModel().getId();
            dto.modelName = b.getModel().getName();
        }
        if (b.getProblemType() != null) {
            dto.problemTypeId = b.getProblemType().getId();
            dto.problemTypeTitle = b.getProblemType().getTitle();
        }
        dto.problemDescription = b.getProblemDescription();
        if (b.getAddress() != null) {
            dto.address = AddressDto.fromEntity(b.getAddress());
        }
        dto.scheduledDate = b.getScheduledDate();
        dto.timeSlot = b.getTimeSlot();
        dto.status = b.getStatus();
        dto.inspectionFee = b.getInspectionFee();
        dto.finalAmount = b.getFinalAmount();
        dto.paymentStatus = b.getPaymentStatus();
        dto.cancellationReason = b.getCancellationReason();
        dto.createdAt = b.getCreatedAt();
        dto.updatedAt = b.getUpdatedAt();

        return dto;
    }

    public String getId() { return id; }
    public String getBookingReference() { return bookingReference; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public String getCustomerPhone() { return customerPhone; }
    public String getTechnicianId() { return technicianId; }
    public String getTechnicianName() { return technicianName; }
    public String getTechnicianPhone() { return technicianPhone; }
    public String getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public String getBrandId() { return brandId; }
    public String getBrandName() { return brandName; }
    public String getModelId() { return modelId; }
    public String getModelName() { return modelName; }
    public String getProblemTypeId() { return problemTypeId; }
    public String getProblemTypeTitle() { return problemTypeTitle; }
    public String getProblemDescription() { return problemDescription; }
    public AddressDto getAddress() { return address; }
    public String getScheduledDate() { return scheduledDate; }
    public String getTimeSlot() { return timeSlot; }
    public String getStatus() { return status; }
    public BigDecimal getInspectionFee() { return inspectionFee; }
    public BigDecimal getFinalAmount() { return finalAmount; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getCancellationReason() { return cancellationReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<BookingTimelineDto> getTimeline() { return timeline; }
    public void setTimeline(List<BookingTimelineDto> timeline) { this.timeline = timeline; }
}
