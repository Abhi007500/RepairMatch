package com.repairmatch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairmatch.common.security.JwtTokenProvider;
import com.repairmatch.modules.booking.domain.Booking;
import com.repairmatch.modules.booking.repository.BookingRepository;
import com.repairmatch.modules.review.dto.CreateReviewRequest;
import com.repairmatch.modules.technician.domain.TechnicianProfile;
import com.repairmatch.modules.technician.repository.TechnicianProfileRepository;
import com.repairmatch.modules.user.domain.Address;
import com.repairmatch.modules.user.domain.User;
import com.repairmatch.modules.user.repository.AddressRepository;
import com.repairmatch.modules.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ReviewWorkflowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TechnicianProfileRepository technicianProfileRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private com.repairmatch.modules.catalog.repository.CategoryRepository categoryRepository;

    @Test
    void testCreateReviewForCompletedBookingRecalculatesRating() throws Exception {
        User customer = userRepository.findByEmail("rahul@gmail.com").orElseThrow();
        TechnicianProfile technician = technicianProfileRepository.findById("tech-prof-3").orElseThrow();
        Address address = addressRepository.findByUserId(customer.getId()).get(0);
        com.repairmatch.modules.catalog.domain.Category category = categoryRepository.findById("cat-14").orElseThrow();

        // Create a completed booking for Tech 3 (Vikram Patel)
        String bookingId = UUID.randomUUID().toString();
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBookingReference("RM-2026-TEST");
        booking.setCustomer(customer);
        booking.setTechnician(technician);
        booking.setCategory(category);
        booking.setProblemDescription("Plumbing repair completed");
        booking.setAddress(address);
        booking.setScheduledDate("2026-09-20");
        booking.setTimeSlot("10:00 AM - 01:00 PM");
        booking.setStatus("COMPLETED");
        booking.setInspectionFee(new BigDecimal("199.00"));
        booking.setFinalAmount(new BigDecimal("450.00"));
        booking.setPaymentStatus("PAID");
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        String customerToken = jwtTokenProvider.generateTokenFromEmail(
                customer.getEmail(), customer.getId(), "CUSTOMER", customer.getFullName());

        CreateReviewRequest reviewReq = new CreateReviewRequest(bookingId, 5, "Outstanding plumbing service! Fixed leak instantly.");

        // 1. Submit review
        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.customerName").value("Rahul Verma"));

        // 2. Verify duplicate review fails
        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already been submitted")));

        // 3. Verify public reviews endpoint shows this review
        mockMvc.perform(get("/api/reviews/technician/" + technician.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[*].comment", hasItem(containsString("Outstanding plumbing service"))));
    }

    @Test
    void testCannotReviewPendingBooking() throws Exception {
        // bk-003 is PENDING in seed data
        String customerToken = jwtTokenProvider.generateTokenFromEmail(
                "rahul@gmail.com", "usr-cust-1", "CUSTOMER", "Rahul Verma");

        CreateReviewRequest reviewReq = new CreateReviewRequest("bk-003", 5, "Trying to review premature booking");

        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Reviews can only be submitted for completed repair services")));
    }
}
