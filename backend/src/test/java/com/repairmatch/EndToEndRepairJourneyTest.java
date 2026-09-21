package com.repairmatch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairmatch.modules.auth.dto.LoginRequest;
import com.repairmatch.modules.auth.dto.RegisterRequest;
import com.repairmatch.modules.booking.dto.CreateBookingRequest;
import com.repairmatch.modules.booking.dto.UpdateBookingStatusRequest;
import com.repairmatch.modules.matching.dto.MatchRequest;
import com.repairmatch.modules.review.dto.CreateReviewRequest;
import com.repairmatch.modules.user.dto.CreateAddressRequest;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EndToEndRepairJourneyTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCompleteCustomerAndTechnicianJourney() throws Exception {
        // 1. CUSTOMER REGISTRATION
        RegisterRequest registerReq = new RegisterRequest(
                "anita.journey@gmail.com",
                "password123",
                "Anita Desai",
                "+91 9911223344",
                "CUSTOMER"
        );

        MvcResult regResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        String customerToken = objectMapper.readTree(regResult.getResponse().getContentAsString()).get("token").asText();

        // 2. CUSTOMER ADDS SERVICE ADDRESS
        CreateAddressRequest addrReq = new CreateAddressRequest(
                "House 55, Indiranagar 100ft Road", "Bengaluru", "Karnataka", "560038",
                12.9719, 77.6412, true
        );

        MvcResult addrResult = mockMvc.perform(post("/api/customer/addresses")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addrReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String addressId = objectMapper.readTree(addrResult.getResponse().getContentAsString()).get("id").asText();
        assertNotNull(addressId);

        // 3. CUSTOMER RUNS MATCHING ENGINE FOR LAPTOP DISPLAY FIX
        MatchRequest matchReq = new MatchRequest(
                "cat-02", // Laptops
                "brd-01", // Apple
                "mod-04", // MacBook Air M2
                "prb-03", // Display broken
                12.9719,
                77.6412
        );
        matchReq.setScheduledDate("2026-10-15");
        matchReq.setTimeSlot("09:00 AM - 12:00 PM");

        MvcResult matchResult = mockMvc.perform(post("/api/matching/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matchReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andReturn();

        JsonNode matches = objectMapper.readTree(matchResult.getResponse().getContentAsString());
        String selectedTechId = matches.get(0).get("technicianId").asText();

        // 4. CUSTOMER BOOKS APPOINTMENT
        CreateBookingRequest bookingReq = new CreateBookingRequest(
                selectedTechId,
                "cat-02",
                "brd-01",
                "mod-04",
                "prb-03",
                "MacBook Air display has horizontal purple lines after lid pressure.",
                addressId,
                "2026-10-15",
                "09:00 AM - 12:00 PM"
        );

        MvcResult bookResult = mockMvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn();

        String bookingId = objectMapper.readTree(bookResult.getResponse().getContentAsString()).get("id").asText();

        // 5. TECHNICIAN LOGS IN
        LoginRequest techLogin = new LoginRequest("rajesh.tech@repairmatch.com", "password123");
        MvcResult techLoginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(techLogin)))
                .andExpect(status().isOk())
                .andReturn();

        String techToken = objectMapper.readTree(techLoginResult.getResponse().getContentAsString()).get("token").asText();

        // 6. TECHNICIAN ACCEPTS JOB
        UpdateBookingStatusRequest acceptReq = new UpdateBookingStatusRequest("ACCEPTED", "Confirmed appointment for Oct 15th morning");
        mockMvc.perform(put("/api/bookings/" + bookingId + "/status")
                        .header("Authorization", "Bearer " + techToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(acceptReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        // 7. TECHNICIAN STARTS WORK
        UpdateBookingStatusRequest inProgReq = new UpdateBookingStatusRequest("IN_PROGRESS", "Inspected display ribbon cable and screen panel");
        mockMvc.perform(put("/api/bookings/" + bookingId + "/status")
                        .header("Authorization", "Bearer " + techToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inProgReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        // 8. TECHNICIAN COMPLETES SERVICE & ISSUES INVOICE
        UpdateBookingStatusRequest completeReq = new UpdateBookingStatusRequest("COMPLETED", "Display panel replaced and 100% functional");
        completeReq.setFinalAmount(new BigDecimal("3500.00"));
        mockMvc.perform(put("/api/bookings/" + bookingId + "/status")
                        .header("Authorization", "Bearer " + techToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(completeReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.paymentStatus").value("PAID"))
                .andExpect(jsonPath("$.finalAmount").value(3500.00));

        // 9. CUSTOMER SUBMITS 5-STAR REVIEW
        CreateReviewRequest reviewReq = new CreateReviewRequest(bookingId, 5, "Brilliant service! Fixed my MacBook screen fast and flawlessly.");
        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(5));

        // 10. CUSTOMER VIEWS BOOKING HISTORY WITH TIMELINE
        mockMvc.perform(get("/api/bookings/" + bookingId)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.timeline", hasSize(4)));
    }
}
