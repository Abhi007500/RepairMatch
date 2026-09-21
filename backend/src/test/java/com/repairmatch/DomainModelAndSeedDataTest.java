package com.repairmatch;

import com.repairmatch.modules.booking.domain.Booking;
import com.repairmatch.modules.booking.repository.BookingRepository;
import com.repairmatch.modules.catalog.domain.Category;
import com.repairmatch.modules.catalog.repository.BrandRepository;
import com.repairmatch.modules.catalog.repository.CategoryRepository;
import com.repairmatch.modules.technician.domain.TechnicianProfile;
import com.repairmatch.modules.technician.repository.TechnicianProfileRepository;
import com.repairmatch.modules.user.domain.User;
import com.repairmatch.modules.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DomainModelAndSeedDataTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TechnicianProfileRepository technicianProfileRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    @Transactional
    void verifySeedCategoriesAreLoaded() {
        List<Category> categories = categoryRepository.findAllByOrderByDisplayOrderAsc();
        assertEquals(16, categories.size(), "Should have all 16 seed categories");

        Category smartphones = categoryRepository.findBySlug("smartphones").orElseThrow();
        assertTrue(smartphones.isRequiresBrandAndModel(), "Smartphones should require brand and model");

        Category plumbing = categoryRepository.findBySlug("plumbing").orElseThrow();
        assertFalse(plumbing.isRequiresBrandAndModel(), "Plumbing should NOT require brand and model");
    }

    @Test
    void verifySeedUsersAndTechsAreLoaded() {
        User admin = userRepository.findByEmail("admin@repairmatch.com").orElseThrow();
        assertEquals("ADMIN", admin.getRole());

        User customer = userRepository.findByEmail("rahul@gmail.com").orElseThrow();
        assertEquals("CUSTOMER", customer.getRole());

        User techUser = userRepository.findByEmail("rajesh.tech@repairmatch.com").orElseThrow();
        assertEquals("TECHNICIAN", techUser.getRole());

        TechnicianProfile profile = technicianProfileRepository.findByUserId(techUser.getId()).orElseThrow();
        assertEquals("VERIFIED", profile.getVerificationStatus());
        assertTrue(profile.getExperienceYears() >= 8);
    }

    @Test
    @Transactional
    void verifyTechnicianQueryForCategory() {
        // Tech 1 is registered for cat-01 (Smartphones)
        List<TechnicianProfile> smartphoneTechs = technicianProfileRepository.findVerifiedAndAvailableByCategoryId("cat-01");
        assertFalse(smartphoneTechs.isEmpty(), "Should find verified technician for smartphones");
        assertTrue(smartphoneTechs.stream().anyMatch(t -> t.getUser().getFullName().contains("Rajesh")));
    }

    @Test
    void verifySeedBookingsAreLoaded() {
        List<Booking> bookings = bookingRepository.findAll();
        assertTrue(bookings.size() >= 3, "Should have at least 3 demo bookings in various states");

        Booking completedBooking = bookingRepository.findByBookingReference("RM-2026-0001").orElseThrow();
        assertEquals("COMPLETED", completedBooking.getStatus());
        assertEquals("PAID", completedBooking.getPaymentStatus());
    }
}
