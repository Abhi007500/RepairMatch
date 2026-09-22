package com.repairmatch.modules.booking.domain;

import com.repairmatch.modules.catalog.domain.Brand;
import com.repairmatch.modules.catalog.domain.Category;
import com.repairmatch.modules.catalog.domain.Model;
import com.repairmatch.modules.catalog.domain.ProblemType;
import com.repairmatch.modules.technician.domain.TechnicianProfile;
import com.repairmatch.modules.user.domain.Address;
import com.repairmatch.modules.user.domain.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "bookings")
@CompoundIndexes({
    @CompoundIndex(name = "booking_slot_conflict_idx", def = "{'technicianId': 1, 'scheduledDate': 1, 'timeSlot': 1, 'status': 1}"),
    @CompoundIndex(name = "booking_cust_created_idx", def = "{'customerId': 1, 'createdAt': -1}"),
    @CompoundIndex(name = "booking_tech_created_idx", def = "{'technicianId': 1, 'createdAt': -1}")
})
public class Booking {

    @Id
    private String id;

    @Indexed(unique = true)
    private String bookingReference;

    @DBRef
    private User customer;

    @Indexed
    private String customerId;

    @DBRef
    private TechnicianProfile technician;

    @Indexed
    private String technicianId;

    @DBRef
    private Category category;

    @Indexed
    private String categoryId;

    @DBRef
    private Brand brand;

    @DBRef
    private Model model;

    @DBRef
    private ProblemType problemType;

    private String problemDescription;

    @DBRef
    private Address address;

    @Indexed
    private String scheduledDate;

    @Indexed
    private String timeSlot;

    @Indexed
    private String status; // PENDING, ACCEPTED, REJECTED, IN_PROGRESS, COMPLETED, CANCELLED

    private BigDecimal inspectionFee;
    private BigDecimal finalAmount;

    @Indexed
    private String paymentStatus = "PENDING"; // PENDING, PAID, REFUNDED

    private String cancellationReason;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Version
    private Long version;

    public Booking() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public User getCustomer() { return customer; }
    public void setCustomer(User customer) {
        this.customer = customer;
        if (customer != null) {
            this.customerId = customer.getId();
        }
    }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public TechnicianProfile getTechnician() { return technician; }
    public void setTechnician(TechnicianProfile technician) {
        this.technician = technician;
        if (technician != null) {
            this.technicianId = technician.getId();
        }
    }

    public String getTechnicianId() { return technicianId; }
    public void setTechnicianId(String technicianId) { this.technicianId = technicianId; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) {
        this.category = category;
        if (category != null) {
            this.categoryId = category.getId();
        }
    }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public Brand getBrand() { return brand; }
    public void setBrand(Brand brand) { this.brand = brand; }

    public Model getModel() { return model; }
    public void setModel(Model model) { this.model = model; }

    public ProblemType getProblemType() { return problemType; }
    public void setProblemType(ProblemType problemType) { this.problemType = problemType; }

    public String getProblemDescription() { return problemDescription; }
    public void setProblemDescription(String problemDescription) { this.problemDescription = problemDescription; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public String getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(String scheduledDate) { this.scheduledDate = scheduledDate; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getInspectionFee() { return inspectionFee; }
    public void setInspectionFee(BigDecimal inspectionFee) { this.inspectionFee = inspectionFee; }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
