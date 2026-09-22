package com.repairmatch.common.config;

import com.repairmatch.modules.booking.domain.Booking;
import com.repairmatch.modules.booking.domain.BookingTimeline;
import com.repairmatch.modules.booking.repository.BookingRepository;
import com.repairmatch.modules.booking.repository.BookingTimelineRepository;
import com.repairmatch.modules.catalog.domain.Brand;
import com.repairmatch.modules.catalog.domain.Category;
import com.repairmatch.modules.catalog.domain.Model;
import com.repairmatch.modules.catalog.domain.ProblemType;
import com.repairmatch.modules.catalog.repository.BrandRepository;
import com.repairmatch.modules.catalog.repository.CategoryRepository;
import com.repairmatch.modules.catalog.repository.ModelRepository;
import com.repairmatch.modules.catalog.repository.ProblemTypeRepository;
import com.repairmatch.modules.review.domain.Review;
import com.repairmatch.modules.review.repository.ReviewRepository;
import com.repairmatch.modules.technician.domain.TechnicianProfile;
import com.repairmatch.modules.technician.repository.TechnicianProfileRepository;
import com.repairmatch.modules.user.domain.Address;
import com.repairmatch.modules.user.domain.User;
import com.repairmatch.modules.user.repository.AddressRepository;
import com.repairmatch.modules.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class MongoDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MongoDataInitializer.class);
    private static final String BC_PASSWORD = "$2a$10$K6802WmKSXx.Lkd1fEw2nORIpER5XEHkfAif/A7eNRhMV5F3ftl2K"; // password123

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final ProblemTypeRepository problemTypeRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final TechnicianProfileRepository technicianProfileRepository;
    private final BookingRepository bookingRepository;
    private final BookingTimelineRepository bookingTimelineRepository;
    private final ReviewRepository reviewRepository;

    public MongoDataInitializer(
            CategoryRepository categoryRepository,
            BrandRepository brandRepository,
            ModelRepository modelRepository,
            ProblemTypeRepository problemTypeRepository,
            UserRepository userRepository,
            AddressRepository addressRepository,
            TechnicianProfileRepository technicianProfileRepository,
            BookingRepository bookingRepository,
            BookingTimelineRepository bookingTimelineRepository,
            ReviewRepository reviewRepository) {
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.modelRepository = modelRepository;
        this.problemTypeRepository = problemTypeRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.technicianProfileRepository = technicianProfileRepository;
        this.bookingRepository = bookingRepository;
        this.bookingTimelineRepository = bookingTimelineRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            log.info("MongoDB database already initialized. Skipping seed data.");
            return;
        }

        log.info("Initializing MongoDB database with RepairMatch baseline data...");

        // 1. SEED BRANDS (16 Brands)
        Map<String, Brand> bMap = new HashMap<>();
        String[][] brandsData = {
                {"brd-01", "Apple", "apple"},
                {"brd-02", "Samsung", "samsung"},
                {"brd-03", "Xiaomi", "xiaomi"},
                {"brd-04", "OnePlus", "oneplus"},
                {"brd-05", "Dell", "dell"},
                {"brd-06", "HP", "hp"},
                {"brd-07", "Lenovo", "lenovo"},
                {"brd-08", "LG", "lg"},
                {"brd-09", "Sony", "sony"},
                {"brd-10", "Daikin", "daikin"},
                {"brd-11", "Voltas", "voltas"},
                {"brd-12", "Whirlpool", "whirlpool"},
                {"brd-13", "Kent", "kent"},
                {"brd-14", "Aquaguard", "aquaguard"},
                {"brd-15", "Havells", "havells"},
                {"brd-16", "Luminous", "luminous"}
        };
        for (String[] bd : brandsData) {
            Brand b = new Brand(bd[0], bd[1], bd[2]);
            brandRepository.save(b);
            bMap.put(b.getId(), b);
        }

        // 2. SEED CATEGORIES (16 Categories)
        Map<String, Category> cMap = new HashMap<>();
        Object[][] catsData = {
                {"cat-01", "Smartphones", "smartphones", "Screen, battery, motherboard & camera repair for all mobile devices", "Smartphone", true, 1, List.of("brd-01", "brd-02", "brd-03", "brd-04")},
                {"cat-02", "Laptops", "laptops", "Hardware upgrades, motherboard repair, display & keyboard replacements", "Laptop", true, 2, List.of("brd-01", "brd-05", "brd-06", "brd-07")},
                {"cat-03", "Tablets", "tablets", "Screen repair, charging port fix, and battery service for iPads & tabs", "Tablet", true, 3, List.of("brd-01", "brd-02", "brd-07")},
                {"cat-04", "TVs", "tvs", "LED, OLED, QLED panel repair, power board and audio troubleshooting", "Tv", true, 4, List.of("brd-02", "brd-08", "brd-09")},
                {"cat-05", "ACs", "acs", "Air conditioner installation, gas charging, deep cleaning & PCB repair", "Wind", true, 5, List.of("brd-08", "brd-10", "brd-11")},
                {"cat-06", "Refrigerators", "refrigerators", "Compressor repair, gas filling, thermostat and cooling fixes", "Refrigerator", true, 6, List.of("brd-02", "brd-08", "brd-12")},
                {"cat-07", "Washing Machines", "washing-machines", "Motor repair, drum replacement, water leakage and drainage issues", "Disc", true, 7, List.of("brd-02", "brd-08", "brd-12")},
                {"cat-08", "Geysers", "geysers", "Heating element replacement, thermostat fix and tank leakage repair", "Flame", true, 8, List.of("brd-15", "brd-11")},
                {"cat-09", "Microwaves", "microwaves", "Magnetron replacement, touchpad issues, heating failure diagnosis", "Microwave", true, 9, List.of("brd-08", "brd-02")},
                {"cat-10", "RO / Water Purifiers", "ro-purifiers", "Filter replacement, membrane change, pump repair and TDS calibration", "Droplets", true, 10, List.of("brd-13", "brd-14")},
                {"cat-11", "Fans / Coolers", "fans-coolers", "Motor rewinding, pump replacement, blade repair and wiring checks", "Fan", true, 11, List.of("brd-15")},
                {"cat-12", "Inverters", "inverters", "UPS board diagnostics, battery maintenance, overload tripping fixes", "BatteryCharging", true, 12, List.of("brd-16")},
                {"cat-13", "Electrical Repair", "electrical-repair", "Short circuit, switchboard replacement, fuse, MCB & home rewiring", "Zap", false, 13, Collections.emptyList()},
                {"cat-14", "Plumbing", "plumbing", "Pipe leaks, tap fixing, bathroom fittings, drain unblocking & motor installation", "Wrench", false, 14, Collections.emptyList()},
                {"cat-15", "Carpentry", "carpentry", "Door & lock repairs, furniture assembly, hinge fixing and woodwork", "Hammer", false, 15, Collections.emptyList()},
                {"cat-16", "Furniture Repair", "furniture-repair", "Sofa refurbishment, polishing, cushion replacement and broken wood joints", "Armchair", false, 16, Collections.emptyList()}
        };

        for (Object[] cd : catsData) {
            Category c = new Category();
            c.setId((String) cd[0]);
            c.setName((String) cd[1]);
            c.setSlug((String) cd[2]);
            c.setDescription((String) cd[3]);
            c.setIconName((String) cd[4]);
            c.setRequiresBrandAndModel((Boolean) cd[5]);
            c.setDisplayOrder((Integer) cd[6]);
            c.setCreatedAt(LocalDateTime.now());

            @SuppressWarnings("unchecked")
            List<String> bIds = (List<String>) cd[7];
            Set<Brand> catBrands = new HashSet<>();
            for (String bid : bIds) {
                Brand b = bMap.get(bid);
                if (b != null) {
                    catBrands.add(b);
                    b.getCategoryIds().add(c.getId());
                    brandRepository.save(b);
                }
            }
            c.setBrands(catBrands);
            categoryRepository.save(c);
            cMap.put(c.getId(), c);
        }

        // 3. SEED PROBLEM TYPES
        Map<String, ProblemType> pMap = new HashMap<>();
        Object[][] problemsData = {
                {"prb-01", "cat-01", "Cracked Screen / Glass Replacement", "Physical damage to display, touch unresponsive or lines on screen", new BigDecimal("2500.00")},
                {"prb-02", "cat-01", "Battery Draining Quickly / Swollen", "Battery health degraded below 80% or device turns off randomly", new BigDecimal("1400.00")},
                {"prb-03", "cat-02", "Display / Hinge Broken", "Screen flickering, cracked panel or broken physical display hinge", new BigDecimal("3500.00")},
                {"prb-04", "cat-02", "Laptop Not Powering On / Motherboard Issue", "No power response, charging light blinks, liquid damage or short circuit", new BigDecimal("2200.00")},
                {"prb-05", "cat-05", "Not Cooling / Low Airflow", "Compressor runs but air is warm, air filters blocked or fan issue", new BigDecimal("800.00")},
                {"prb-06", "cat-05", "Gas Leakage & Refill", "Complete loss of refrigerant gas, needs leak testing and R32/R410 refill", new BigDecimal("2400.00")},
                {"prb-07", "cat-14", "Water Pipe Leakage / Seepage", "Concealed or exposed water pipe leaking, causing seepage or pressure loss", new BigDecimal("600.00")},
                {"prb-08", "cat-14", "Tap / Faucet Replacement or Jamming", "Dripping tap, broken ceramic disc or low water pressure from faucet", new BigDecimal("350.00")},
                {"prb-09", "cat-13", "Switchboard Sparking / Burnt Socket", "Loose electrical contacts, burning smell or non-functional power socket", new BigDecimal("400.00")},
                {"prb-10", "cat-15", "Door Lock / Latch Stuck or Broken", "Key jammed, mortise lock replacement or latch alignment issue", new BigDecimal("550.00")}
        };
        for (Object[] pd : problemsData) {
            ProblemType pt = new ProblemType();
            pt.setId((String) pd[0]);
            pt.setCategory(cMap.get((String) pd[1]));
            pt.setTitle((String) pd[2]);
            pt.setDescription((String) pd[3]);
            pt.setTypicalPriceEstimate((BigDecimal) pd[4]);
            pt.setCreatedAt(LocalDateTime.now());
            problemTypeRepository.save(pt);
            pMap.put(pt.getId(), pt);
        }

        // 4. SEED MODELS
        Map<String, Model> mMap = new HashMap<>();
        String[][] modelsData = {
                {"mod-01", "brd-01", "cat-01", "iPhone 14 Pro"},
                {"mod-02", "brd-01", "cat-01", "iPhone 13"},
                {"mod-03", "brd-02", "cat-01", "Galaxy S23"},
                {"mod-04", "brd-01", "cat-02", "MacBook Air M2"},
                {"mod-05", "brd-05", "cat-02", "XPS 15"},
                {"mod-06", "brd-06", "cat-02", "Pavilion 14"},
                {"mod-07", "brd-10", "cat-05", "FTKG 1.5 Ton 5 Star Inverter AC"},
                {"mod-08", "brd-11", "cat-05", "Vectra 1.5 Ton Split AC"},
                {"mod-09", "brd-02", "cat-06", "Convertible 5-in-1 Double Door 324L"},
                {"mod-10", "brd-13", "cat-10", "Grand Plus RO + UV + UF 9L"}
        };
        for (String[] md : modelsData) {
            Model m = new Model();
            m.setId(md[0]);
            m.setBrand(bMap.get(md[1]));
            m.setCategory(cMap.get(md[2]));
            m.setName(md[3]);
            m.setCreatedAt(LocalDateTime.now());
            modelRepository.save(m);
            mMap.put(m.getId(), m);
        }

        // 5. SEED USERS
        Map<String, User> uMap = new HashMap<>();
        String[][] usersData = {
                {"usr-admin", "admin@repairmatch.com", "System Administrator", "+91 9999900001", "ADMIN"},
                {"usr-cust-1", "rahul@gmail.com", "Rahul Verma", "+91 9876543210", "CUSTOMER"},
                {"usr-cust-2", "priya@gmail.com", "Priya Sharma", "+91 9876543211", "CUSTOMER"},
                {"usr-tech-1", "rajesh.tech@repairmatch.com", "Rajesh Kumar (Tech Specialist)", "+91 9811122233", "TECHNICIAN"},
                {"usr-tech-2", "amit.tech@repairmatch.com", "Amit Singh (Appliance Master)", "+91 9822233344", "TECHNICIAN"},
                {"usr-tech-3", "vikram.plumber@repairmatch.com", "Vikram Patel (Expert Plumber)", "+91 9833344455", "TECHNICIAN"},
                {"usr-tech-4", "suresh.electric@repairmatch.com", "Suresh Nair (Licensed Electrician)", "+91 9844455566", "TECHNICIAN"},
                {"usr-tech-5", "new.tech@repairmatch.com", "Mohan Lal (Unverified Tech)", "+91 9855566677", "TECHNICIAN"}
        };
        for (String[] ud : usersData) {
            User u = new User(ud[0], ud[1], BC_PASSWORD, ud[2], ud[3], ud[4]);
            userRepository.save(u);
            uMap.put(u.getId(), u);
        }

        // 6. SEED ADDRESSES
        Map<String, Address> aMap = new HashMap<>();
        Address addr1 = new Address();
        addr1.setId("addr-c1");
        addr1.setUser(uMap.get("usr-cust-1"));
        addr1.setStreet("Flat 402, Green Glen Layout, Bellandur");
        addr1.setCity("Bengaluru");
        addr1.setState("Karnataka");
        addr1.setPostalCode("560103");
        addr1.setLatitude(12.9298);
        addr1.setLongitude(77.6748);
        addr1.setDefault(true);
        addressRepository.save(addr1);
        aMap.put(addr1.getId(), addr1);

        Address addr2 = new Address();
        addr2.setId("addr-c2");
        addr2.setUser(uMap.get("usr-cust-2"));
        addr2.setStreet("12th Main, HAL 2nd Stage, Indiranagar");
        addr2.setCity("Bengaluru");
        addr2.setState("Karnataka");
        addr2.setPostalCode("560038");
        addr2.setLatitude(12.9719);
        addr2.setLongitude(77.6412);
        addr2.setDefault(true);
        addressRepository.save(addr2);
        aMap.put(addr2.getId(), addr2);

        // 7. SEED TECHNICIAN PROFILES
        Map<String, TechnicianProfile> tMap = new HashMap<>();

        // Tech 1: Smartphones, Laptops, Tablets | Brands: Apple, Dell, Samsung | Problems: Screen, Battery, Display, Motherboard
        TechnicianProfile tp1 = new TechnicianProfile();
        tp1.setId("tech-prof-1");
        tp1.setUser(uMap.get("usr-tech-1"));
        tp1.setBio("Apple & Dell certified hardware engineer with 8+ years experience in chip-level logic board and display repair.");
        tp1.setExperienceYears(8);
        tp1.setVerificationStatus("VERIFIED");
        tp1.setKycDocumentUrl("https://docs.repairmatch.com/kyc/tech-1.pdf");
        tp1.setBaseInspectionFee(new BigDecimal("299.00"));
        tp1.setServiceRadiusKm(15.0);
        tp1.setLatitude(12.9716);
        tp1.setLongitude(77.6413);
        tp1.setAverageRating(4.9);
        tp1.setTotalReviews(48);
        tp1.setCompletedJobsCount(126);
        tp1.setAvailable(true);
        tp1.setCategories(Set.of(cMap.get("cat-01"), cMap.get("cat-02"), cMap.get("cat-03")));
        tp1.setBrands(Set.of(bMap.get("brd-01"), bMap.get("brd-02"), bMap.get("brd-05")));
        tp1.setProblemTypes(Set.of(pMap.get("prb-01"), pMap.get("prb-02"), pMap.get("prb-03"), pMap.get("prb-04")));
        technicianProfileRepository.save(tp1);
        tMap.put(tp1.getId(), tp1);

        // Tech 2: ACs, Refrigerators, Washing Machines | Brands: Daikin, LG, Samsung, Voltas
        TechnicianProfile tp2 = new TechnicianProfile();
        tp2.setId("tech-prof-2");
        tp2.setUser(uMap.get("usr-tech-2"));
        tp2.setBio("Senior refrigeration & HVAC technician specializing in Daikin, LG and Samsung inverters and cooling cycles.");
        tp2.setExperienceYears(6);
        tp2.setVerificationStatus("VERIFIED");
        tp2.setKycDocumentUrl("https://docs.repairmatch.com/kyc/tech-2.pdf");
        tp2.setBaseInspectionFee(new BigDecimal("249.00"));
        tp2.setServiceRadiusKm(12.0);
        tp2.setLatitude(12.9352);
        tp2.setLongitude(77.6245);
        tp2.setAverageRating(4.7);
        tp2.setTotalReviews(32);
        tp2.setCompletedJobsCount(94);
        tp2.setAvailable(true);
        tp2.setCategories(Set.of(cMap.get("cat-05"), cMap.get("cat-06"), cMap.get("cat-07")));
        tp2.setBrands(Set.of(bMap.get("brd-02"), bMap.get("brd-08"), bMap.get("brd-10"), bMap.get("brd-11")));
        tp2.setProblemTypes(Set.of(pMap.get("prb-05"), pMap.get("prb-06")));
        technicianProfileRepository.save(tp2);
        tMap.put(tp2.getId(), tp2);

        // Tech 3: Plumbing
        TechnicianProfile tp3 = new TechnicianProfile();
        tp3.setId("tech-prof-3");
        tp3.setUser(uMap.get("usr-tech-3"));
        tp3.setBio("Licensed master plumber handling residential pipelines, bathroom fittings, motor pumps and concealed water leakage.");
        tp3.setExperienceYears(10);
        tp3.setVerificationStatus("VERIFIED");
        tp3.setKycDocumentUrl("https://docs.repairmatch.com/kyc/tech-3.pdf");
        tp3.setBaseInspectionFee(new BigDecimal("199.00"));
        tp3.setServiceRadiusKm(20.0);
        tp3.setLatitude(12.9784);
        tp3.setLongitude(77.6408);
        tp3.setAverageRating(4.8);
        tp3.setTotalReviews(56);
        tp3.setCompletedJobsCount(180);
        tp3.setAvailable(true);
        tp3.setCategories(Set.of(cMap.get("cat-14")));
        tp3.setProblemTypes(Set.of(pMap.get("prb-07"), pMap.get("prb-08")));
        technicianProfileRepository.save(tp3);
        tMap.put(tp3.getId(), tp3);

        // Tech 4: Electrical Repair & Inverters
        TechnicianProfile tp4 = new TechnicianProfile();
        tp4.setId("tech-prof-4");
        tp4.setUser(uMap.get("usr-tech-4"));
        tp4.setBio("Certified industrial & domestic wireman. Expertise in short circuit detection, MCB upgrades and inverter setups.");
        tp4.setExperienceYears(5);
        tp4.setVerificationStatus("VERIFIED");
        tp4.setKycDocumentUrl("https://docs.repairmatch.com/kyc/tech-4.pdf");
        tp4.setBaseInspectionFee(new BigDecimal("199.00"));
        tp4.setServiceRadiusKm(10.0);
        tp4.setLatitude(12.9279);
        tp4.setLongitude(77.6271);
        tp4.setAverageRating(4.6);
        tp4.setTotalReviews(18);
        tp4.setCompletedJobsCount(52);
        tp4.setAvailable(true);
        tp4.setCategories(Set.of(cMap.get("cat-12"), cMap.get("cat-13")));
        tp4.setProblemTypes(Set.of(pMap.get("prb-09")));
        technicianProfileRepository.save(tp4);
        tMap.put(tp4.getId(), tp4);

        // Tech 5: Junior Unverified
        TechnicianProfile tp5 = new TechnicianProfile();
        tp5.setId("tech-prof-5");
        tp5.setUser(uMap.get("usr-tech-5"));
        tp5.setBio("Junior technician with general handyman and electronics knowledge awaiting document approval.");
        tp5.setExperienceYears(1);
        tp5.setVerificationStatus("PENDING");
        tp5.setKycDocumentUrl("https://docs.repairmatch.com/kyc/tech-5.pdf");
        tp5.setBaseInspectionFee(new BigDecimal("149.00"));
        tp5.setServiceRadiusKm(8.0);
        tp5.setLatitude(12.9500);
        tp5.setLongitude(77.6000);
        tp5.setAverageRating(0.0);
        tp5.setTotalReviews(0);
        tp5.setCompletedJobsCount(0);
        tp5.setAvailable(true);
        technicianProfileRepository.save(tp5);
        tMap.put(tp5.getId(), tp5);

        // 8. SEED BOOKINGS
        Booking b1 = new Booking();
        b1.setId("bk-001");
        b1.setBookingReference("RM-2026-0001");
        b1.setCustomer(uMap.get("usr-cust-2"));
        b1.setTechnician(tMap.get("tech-prof-1"));
        b1.setCategory(cMap.get("cat-01"));
        b1.setBrand(bMap.get("brd-01"));
        b1.setModel(mMap.get("mod-01"));
        b1.setProblemType(pMap.get("prb-01"));
        b1.setProblemDescription("Screen cracked after accidental drop from table. Glass shattered but OLED displays faintly.");
        b1.setAddress(aMap.get("addr-c2"));
        b1.setScheduledDate("2026-09-18");
        b1.setTimeSlot("10:00 AM - 01:00 PM");
        b1.setStatus("COMPLETED");
        b1.setInspectionFee(new BigDecimal("299.00"));
        b1.setFinalAmount(new BigDecimal("2799.00"));
        b1.setPaymentStatus("PAID");
        bookingRepository.save(b1);

        Booking b2 = new Booking();
        b2.setId("bk-002");
        b2.setBookingReference("RM-2026-0002");
        b2.setCustomer(uMap.get("usr-cust-1"));
        b2.setTechnician(tMap.get("tech-prof-2"));
        b2.setCategory(cMap.get("cat-05"));
        b2.setBrand(bMap.get("brd-10"));
        b2.setModel(mMap.get("mod-07"));
        b2.setProblemType(pMap.get("prb-05"));
        b2.setProblemDescription("AC blowing room temperature air instead of cool air. Filter cleaned already.");
        b2.setAddress(aMap.get("addr-c1"));
        b2.setScheduledDate("2026-09-22");
        b2.setTimeSlot("02:00 PM - 05:00 PM");
        b2.setStatus("ACCEPTED");
        b2.setInspectionFee(new BigDecimal("249.00"));
        b2.setPaymentStatus("PENDING");
        bookingRepository.save(b2);

        Booking b3 = new Booking();
        b3.setId("bk-003");
        b3.setBookingReference("RM-2026-0003");
        b3.setCustomer(uMap.get("usr-cust-1"));
        b3.setTechnician(tMap.get("tech-prof-3"));
        b3.setCategory(cMap.get("cat-14"));
        b3.setProblemType(pMap.get("prb-07"));
        b3.setProblemDescription("Kitchen sink drain pipe leaking water into under-counter cabinet.");
        b3.setAddress(aMap.get("addr-c1"));
        b3.setScheduledDate("2026-09-23");
        b3.setTimeSlot("09:00 AM - 12:00 PM");
        b3.setStatus("PENDING");
        b3.setInspectionFee(new BigDecimal("199.00"));
        b3.setPaymentStatus("PENDING");
        bookingRepository.save(b3);

        // 9. SEED BOOKING TIMELINE
        bookingTimelineRepository.save(new BookingTimeline("bt-01", b1, "PENDING", "Booking request initiated by customer"));
        bookingTimelineRepository.save(new BookingTimeline("bt-02", b1, "ACCEPTED", "Technician Rajesh Kumar accepted the service request"));
        bookingTimelineRepository.save(new BookingTimeline("bt-03", b1, "IN_PROGRESS", "Technician arrived at customer premise and began display replacement"));
        bookingTimelineRepository.save(new BookingTimeline("bt-04", b1, "COMPLETED", "Original OLED display fitted and tested. Customer approved test."));

        bookingTimelineRepository.save(new BookingTimeline("bt-05", b2, "PENDING", "Booking request created"));
        bookingTimelineRepository.save(new BookingTimeline("bt-06", b2, "ACCEPTED", "Technician Amit Singh confirmed appointment for 2026-09-22"));

        bookingTimelineRepository.save(new BookingTimeline("bt-07", b3, "PENDING", "Awaiting technician confirmation"));

        // 10. SEED REVIEWS
        Review rev1 = new Review(
                "rev-001",
                b1,
                uMap.get("usr-cust-2"),
                tMap.get("tech-prof-1"),
                5,
                "Rajesh did an exceptional job! He replaced my iPhone 14 Pro screen in under 45 minutes with genuine parts. Very polite and professional."
        );
        reviewRepository.save(rev1);

        log.info("MongoDB database successfully seeded with RepairMatch baseline data.");
    }
}
