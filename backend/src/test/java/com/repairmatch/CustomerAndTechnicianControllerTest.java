package com.repairmatch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairmatch.common.security.JwtTokenProvider;
import com.repairmatch.modules.technician.dto.UpdateTechnicianProfileRequest;
import com.repairmatch.modules.user.dto.CreateAddressRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.test.annotation.DirtiesContext(classMode = org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS)
class CustomerAndTechnicianControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void testCustomerAddAndGetAddresses() throws Exception {
        String token = jwtTokenProvider.generateTokenFromEmail(
                "rahul@gmail.com", "usr-cust-1", "CUSTOMER", "Rahul Verma");

        CreateAddressRequest req = new CreateAddressRequest(
                "Flat 101, Palm Meadows", "Bengaluru", "Karnataka", "560066",
                12.9591, 77.7440, false);

        mockMvc.perform(post("/api/customer/addresses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.street").value("Flat 101, Palm Meadows"))
                .andExpect(jsonPath("$.postalCode").value("560066"));

        mockMvc.perform(get("/api/customer/addresses")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    void testTechnicianUpdateProfileAndAvailability() throws Exception {
        String token = jwtTokenProvider.generateTokenFromEmail(
                "rajesh.tech@repairmatch.com", "usr-tech-1", "TECHNICIAN", "Rajesh Kumar");

        UpdateTechnicianProfileRequest updateReq = new UpdateTechnicianProfileRequest();
        updateReq.setBio("Updated bio: Master technician with 9 years experience.");
        updateReq.setBaseInspectionFee(new BigDecimal("349.00"));
        updateReq.setServiceRadiusKm(20.0);
        updateReq.setCategoryIds(List.of("cat-01", "cat-02"));

        mockMvc.perform(put("/api/technician/profile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bio", containsString("Master technician")))
                .andExpect(jsonPath("$.baseInspectionFee").value(349.00))
                .andExpect(jsonPath("$.serviceRadiusKm").value(20.0));

        mockMvc.perform(patch("/api/technician/availability?available=false")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Restore availability for subsequent tests
        mockMvc.perform(patch("/api/technician/availability?available=true")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testAdminPendingVerificationAndPlatformStats() throws Exception {
        String adminToken = jwtTokenProvider.generateTokenFromEmail(
                "admin@repairmatch.com", "usr-admin", "ADMIN", "System Administrator");

        // 1. Check pending technicians (Mohan Lal is pending in seed data)
        mockMvc.perform(get("/api/admin/technicians/pending")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].verificationStatus").value("PENDING"));

        // 2. Approve technician
        mockMvc.perform(put("/api/admin/technicians/tech-prof-5/verify?status=VERIFIED")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationStatus").value("VERIFIED"));

        // 3. Platform stats
        mockMvc.perform(get("/api/admin/stats")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers", greaterThanOrEqualTo(5)))
                .andExpect(jsonPath("$.verifiedTechnicians", greaterThanOrEqualTo(4)));
    }
}
