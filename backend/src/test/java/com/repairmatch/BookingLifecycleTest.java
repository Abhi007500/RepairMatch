package com.repairmatch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairmatch.common.security.JwtTokenProvider;
import com.repairmatch.modules.booking.dto.CancelBookingRequest;
import com.repairmatch.modules.booking.dto.CreateBookingRequest;
import com.repairmatch.modules.booking.dto.UpdateBookingStatusRequest;
import com.repairmatch.modules.technician.domain.TechnicianProfile;
import com.repairmatch.modules.technician.repository.TechnicianProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class BookingLifecycleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private TechnicianProfileRepository technicianProfileRepository;

    @Test
    void testCompleteBookingLifecycleFromPendingToCompleted() throws Exception {
        String customerToken = jwtTokenProvider.generateTokenFromEmail(
                "rahul@gmail.com", "usr-cust-1", "CUSTOMER", "Rahul Verma");

        String techToken = jwtTokenProvider.generateTokenFromEmail(
                "rajesh.tech@repairmatch.com", "usr-tech-1", "TECHNICIAN", "Rajesh Kumar");

        int initialJobsCount = technicianProfileRepository.findById("tech-prof-1").orElseThrow().getCompletedJobsCount();

        // 1. Create Booking (Customer)
        CreateBookingRequest createReq = new CreateBookingRequest(
                "tech-prof-1",
                "cat-01",
                "brd-01",
                "mod-01",
                "prb-01",
                "Screen replacement needed for iPhone 14 Pro",
                "addr-c1",
                "2026-10-05",
                "10:00 AM - 01:00 PM"
        );

        MvcResult createResult = mockMvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.bookingReference", startsWith("RM-2026-")))
                .andExpect(jsonPath("$.timeline", hasSize(1)))
                .andReturn();

        JsonNode root = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String bookingId = root.get("id").asText();

        // 2. Accept Booking (Technician)
        UpdateBookingStatusRequest acceptReq = new UpdateBookingStatusRequest("ACCEPTED", "Confirmed booking for Oct 5th");
        mockMvc.perform(put("/api/bookings/" + bookingId + "/status")
                        .header("Authorization", "Bearer " + techToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(acceptReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.timeline", hasSize(2)));

        // 3. Mark In Progress (Technician arrives)
        UpdateBookingStatusRequest inProgReq = new UpdateBookingStatusRequest("IN_PROGRESS", "Started screen disassembly and diagnostic check");
        mockMvc.perform(put("/api/bookings/" + bookingId + "/status")
                        .header("Authorization", "Bearer " + techToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inProgReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.timeline", hasSize(3)));

        // 4. Mark Completed (Technician finishes with final amount)
        UpdateBookingStatusRequest completeReq = new UpdateBookingStatusRequest("COMPLETED", "New screen installed and tested with customer");
        completeReq.setFinalAmount(new BigDecimal("2800.00"));

        mockMvc.perform(put("/api/bookings/" + bookingId + "/status")
                        .header("Authorization", "Bearer " + techToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(completeReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.paymentStatus").value("PAID"))
                .andExpect(jsonPath("$.finalAmount").value(2800.00))
                .andExpect(jsonPath("$.timeline", hasSize(4)));

        // 5. Verify technician completedJobsCount is incremented!
        TechnicianProfile updatedTech = technicianProfileRepository.findById("tech-prof-1").orElseThrow();
        assertEquals(initialJobsCount + 1, updatedTech.getCompletedJobsCount(),
                "Completed jobs count should be incremented by 1");

        // 6. Test Invalid Transition (Terminal state: cannot transition COMPLETED -> IN_PROGRESS)
        UpdateBookingStatusRequest invalidReq = new UpdateBookingStatusRequest("IN_PROGRESS", "Attempt invalid transition");
        mockMvc.perform(put("/api/bookings/" + bookingId + "/status")
                        .header("Authorization", "Bearer " + techToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid booking status transition")));
    }

    @Test
    void testBookingCancellationFlow() throws Exception {
        String customerToken = jwtTokenProvider.generateTokenFromEmail(
                "priya@gmail.com", "usr-cust-2", "CUSTOMER", "Priya Sharma");

        CreateBookingRequest createReq = new CreateBookingRequest(
                "tech-prof-3",
                "cat-14",
                null,
                null,
                "prb-07",
                "Bathroom pipe leaking",
                "addr-c2",
                "2026-10-10",
                "02:00 PM - 05:00 PM"
        );

        MvcResult createResult = mockMvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String bookingId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        // Cancel
        CancelBookingRequest cancelReq = new CancelBookingRequest("Issue resolved by apartment maintenance.");
        mockMvc.perform(post("/api/bookings/" + bookingId + "/cancel")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.cancellationReason").value("Issue resolved by apartment maintenance."));
    }
}
