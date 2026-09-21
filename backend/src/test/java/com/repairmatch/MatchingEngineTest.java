package com.repairmatch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairmatch.common.util.GeoUtils;
import com.repairmatch.modules.matching.dto.MatchRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MatchingEngineTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGeoUtilsDistanceCalculation() {
        // Distance between Indiranagar (12.9719, 77.6412) and Koramangala (12.9352, 77.6245) is ~4.4 km
        double distance = GeoUtils.calculateDistanceKm(12.9719, 77.6412, 12.9352, 77.6245);
        assertTrue(distance >= 4.0 && distance <= 5.0, "Calculated distance should be approximately 4.4 km");
    }

    @Test
    void testSmartphoneAppleScreenRepairFindsTechSpecialistWithHighScore() throws Exception {
        // Customer location: Indiranagar (12.9719, 77.6412)
        // Request: Smartphone (cat-01), Apple (brd-01), Screen repair (prb-01)
        MatchRequest request = new MatchRequest(
                "cat-01",
                "brd-01",
                "mod-01",
                "prb-01",
                12.9719,
                77.6412
        );

        mockMvc.perform(post("/api/matching/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].technicianId").value("tech-prof-1"))
                .andExpect(jsonPath("$[0].suitabilityScore", greaterThanOrEqualTo(85)))
                .andExpect(jsonPath("$[0].brandMatched").value(true))
                .andExpect(jsonPath("$[0].problemMatched").value(true))
                .andExpect(jsonPath("$[0].matchHighlights", hasItem("Brand Specialist")));
    }

    @Test
    void testHardFilterExcludesTechniciansOutsideServiceRadius() throws Exception {
        // Customer location 100 km away (e.g., Mysore or distant location)
        MatchRequest request = new MatchRequest(
                "cat-01",
                "brd-01",
                null,
                null,
                12.2958, // Mysore latitude (~140 km from Bangalore)
                76.6394
        );

        mockMvc.perform(post("/api/matching/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty())); // Tech 1 service radius is 15 km, so should be filtered out
    }

    @Test
    void testHardFilterExcludesUnverifiedTechnician() throws Exception {
        // Tech 5 is pending verification in seed data. It should NEVER appear in match results.
        MatchRequest request = new MatchRequest(
                "cat-01",
                null,
                null,
                null,
                12.9500,
                77.6000
        );

        mockMvc.perform(post("/api/matching/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].technicianId", not(hasItem("tech-prof-5"))));
    }

    @Test
    void testHardFilterExcludesTechnicianWithSlotConflict() throws Exception {
        // In seed data: bk-002 is ACCEPTED for Tech 2 on date "2026-09-22", slot "02:00 PM - 05:00 PM"
        // Customer requests AC repair for that exact date and slot:
        MatchRequest conflictRequest = new MatchRequest(
                "cat-05", // AC
                "brd-10", // Daikin
                null,
                null,
                12.9340,
                77.6101
        );
        conflictRequest.setScheduledDate("2026-09-22");
        conflictRequest.setTimeSlot("02:00 PM - 05:00 PM");

        mockMvc.perform(post("/api/matching/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conflictRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].technicianId", not(hasItem("tech-prof-2")))); // Tech 2 must be excluded!

        // When requesting a different slot ("09:00 AM - 12:00 PM"), Tech 2 should be included!
        conflictRequest.setTimeSlot("09:00 AM - 12:00 PM");
        mockMvc.perform(post("/api/matching/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conflictRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].technicianId", hasItem("tech-prof-2")));
    }
}
