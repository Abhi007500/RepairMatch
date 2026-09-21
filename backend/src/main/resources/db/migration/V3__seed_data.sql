-- V3__seed_data.sql
-- Seed data for Categories, Brands, Models, Problems, Users, Technicians, Bookings, and Reviews

-- 1. SEED CATEGORIES (16 Categories)
INSERT INTO categories (id, name, slug, description, icon_name, requires_brand_and_model, display_order, created_at) VALUES
('cat-01', 'Smartphones', 'smartphones', 'Screen, battery, motherboard & camera repair for all mobile devices', 'Smartphone', TRUE, 1, CURRENT_TIMESTAMP),
('cat-02', 'Laptops', 'laptops', 'Hardware upgrades, motherboard repair, display & keyboard replacements', 'Laptop', TRUE, 2, CURRENT_TIMESTAMP),
('cat-03', 'Tablets', 'tablets', 'Screen repair, charging port fix, and battery service for iPads & tabs', 'Tablet', TRUE, 3, CURRENT_TIMESTAMP),
('cat-04', 'TVs', 'tvs', 'LED, OLED, QLED panel repair, power board and audio troubleshooting', 'Tv', TRUE, 4, CURRENT_TIMESTAMP),
('cat-05', 'ACs', 'acs', 'Air conditioner installation, gas charging, deep cleaning & PCB repair', 'Wind', TRUE, 5, CURRENT_TIMESTAMP),
('cat-06', 'Refrigerators', 'refrigerators', 'Compressor repair, gas filling, thermostat and cooling fixes', 'Refrigerator', TRUE, 6, CURRENT_TIMESTAMP),
('cat-07', 'Washing Machines', 'washing-machines', 'Motor repair, drum replacement, water leakage and drainage issues', 'Disc', TRUE, 7, CURRENT_TIMESTAMP),
('cat-08', 'Geysers', 'geysers', 'Heating element replacement, thermostat fix and tank leakage repair', 'Flame', TRUE, 8, CURRENT_TIMESTAMP),
('cat-09', 'Microwaves', 'microwaves', 'Magnetron replacement, touchpad issues, heating failure diagnosis', 'Microwave', TRUE, 9, CURRENT_TIMESTAMP),
('cat-10', 'RO / Water Purifiers', 'ro-purifiers', 'Filter replacement, membrane change, pump repair and TDS calibration', 'Droplets', TRUE, 10, CURRENT_TIMESTAMP),
('cat-11', 'Fans / Coolers', 'fans-coolers', 'Motor rewinding, pump replacement, blade repair and wiring checks', 'Fan', TRUE, 11, CURRENT_TIMESTAMP),
('cat-12', 'Inverters', 'inverters', 'UPS board diagnostics, battery maintenance, overload tripping fixes', 'BatteryCharging', TRUE, 12, CURRENT_TIMESTAMP),
('cat-13', 'Electrical Repair', 'electrical-repair', 'Short circuit, switchboard replacement, fuse, MCB & home rewiring', 'Zap', FALSE, 13, CURRENT_TIMESTAMP),
('cat-14', 'Plumbing', 'plumbing', 'Pipe leaks, tap fixing, bathroom fittings, drain unblocking & motor installation', 'Wrench', FALSE, 14, CURRENT_TIMESTAMP),
('cat-15', 'Carpentry', 'carpentry', 'Door & lock repairs, furniture assembly, hinge fixing and woodwork', 'Hammer', FALSE, 15, CURRENT_TIMESTAMP),
('cat-16', 'Furniture Repair', 'furniture-repair', 'Sofa refurbishment, polishing, cushion replacement and broken wood joints', 'Armchair', FALSE, 16, CURRENT_TIMESTAMP);

-- 2. SEED BRANDS
INSERT INTO brands (id, name, slug, created_at) VALUES
('brd-01', 'Apple', 'apple', CURRENT_TIMESTAMP),
('brd-02', 'Samsung', 'samsung', CURRENT_TIMESTAMP),
('brd-03', 'Xiaomi', 'xiaomi', CURRENT_TIMESTAMP),
('brd-04', 'OnePlus', 'oneplus', CURRENT_TIMESTAMP),
('brd-05', 'Dell', 'dell', CURRENT_TIMESTAMP),
('brd-06', 'HP', 'hp', CURRENT_TIMESTAMP),
('brd-07', 'Lenovo', 'lenovo', CURRENT_TIMESTAMP),
('brd-08', 'LG', 'lg', CURRENT_TIMESTAMP),
('brd-09', 'Sony', 'sony', CURRENT_TIMESTAMP),
('brd-10', 'Daikin', 'daikin', CURRENT_TIMESTAMP),
('brd-11', 'Voltas', 'voltas', CURRENT_TIMESTAMP),
('brd-12', 'Whirlpool', 'whirlpool', CURRENT_TIMESTAMP),
('brd-13', 'Kent', 'kent', CURRENT_TIMESTAMP),
('brd-14', 'Aquaguard', 'aquaguard', CURRENT_TIMESTAMP),
('brd-15', 'Havells', 'havells', CURRENT_TIMESTAMP),
('brd-16', 'Luminous', 'luminous', CURRENT_TIMESTAMP);

-- 3. CATEGORY_BRANDS MAPPINGS
INSERT INTO category_brands (category_id, brand_id) VALUES
('cat-01', 'brd-01'), ('cat-01', 'brd-02'), ('cat-01', 'brd-03'), ('cat-01', 'brd-04'),
('cat-02', 'brd-01'), ('cat-02', 'brd-05'), ('cat-02', 'brd-06'), ('cat-02', 'brd-07'),
('cat-03', 'brd-01'), ('cat-03', 'brd-02'), ('cat-03', 'brd-07'),
('cat-04', 'brd-02'), ('cat-04', 'brd-08'), ('cat-04', 'brd-09'),
('cat-05', 'brd-08'), ('cat-05', 'brd-10'), ('cat-05', 'brd-11'),
('cat-06', 'brd-02'), ('cat-06', 'brd-08'), ('cat-06', 'brd-12'),
('cat-07', 'brd-02'), ('cat-07', 'brd-08'), ('cat-07', 'brd-12'),
('cat-08', 'brd-15'), ('cat-08', 'brd-11'),
('cat-09', 'brd-08'), ('cat-09', 'brd-02'),
('cat-10', 'brd-13'), ('cat-10', 'brd-14'),
('cat-11', 'brd-15'),
('cat-12', 'brd-16');

-- 4. SEED MODELS
INSERT INTO models (id, brand_id, category_id, name, created_at) VALUES
('mod-01', 'brd-01', 'cat-01', 'iPhone 14 Pro', CURRENT_TIMESTAMP),
('mod-02', 'brd-01', 'cat-01', 'iPhone 13', CURRENT_TIMESTAMP),
('mod-03', 'brd-02', 'cat-01', 'Galaxy S23', CURRENT_TIMESTAMP),
('mod-04', 'brd-01', 'cat-02', 'MacBook Air M2', CURRENT_TIMESTAMP),
('mod-05', 'brd-05', 'cat-02', 'XPS 15', CURRENT_TIMESTAMP),
('mod-06', 'brd-06', 'cat-02', 'Pavilion 14', CURRENT_TIMESTAMP),
('mod-07', 'brd-10', 'cat-05', 'FTKG 1.5 Ton 5 Star Inverter AC', CURRENT_TIMESTAMP),
('mod-08', 'brd-11', 'cat-05', 'Vectra 1.5 Ton Split AC', CURRENT_TIMESTAMP),
('mod-09', 'brd-02', 'cat-06', 'Convertible 5-in-1 Double Door 324L', CURRENT_TIMESTAMP),
('mod-10', 'brd-13', 'cat-10', 'Grand Plus RO + UV + UF 9L', CURRENT_TIMESTAMP);

-- 5. SEED PROBLEM TYPES
INSERT INTO problem_types (id, category_id, title, description, typical_price_estimate, created_at) VALUES
('prb-01', 'cat-01', 'Cracked Screen / Glass Replacement', 'Physical damage to display, touch unresponsive or lines on screen', 2500.00, CURRENT_TIMESTAMP),
('prb-02', 'cat-01', 'Battery Draining Quickly / Swollen', 'Battery health degraded below 80% or device turns off randomly', 1400.00, CURRENT_TIMESTAMP),
('prb-03', 'cat-02', 'Display / Hinge Broken', 'Screen flickering, cracked panel or broken physical display hinge', 3500.00, CURRENT_TIMESTAMP),
('prb-04', 'cat-02', 'Laptop Not Powering On / Motherboard Issue', 'No power response, charging light blinks, liquid damage or short circuit', 2200.00, CURRENT_TIMESTAMP),
('prb-05', 'cat-05', 'Not Cooling / Low Airflow', 'Compressor runs but air is warm, air filters blocked or fan issue', 800.00, CURRENT_TIMESTAMP),
('prb-06', 'cat-05', 'Gas Leakage & Refill', 'Complete loss of refrigerant gas, needs leak testing and R32/R410 refill', 2400.00, CURRENT_TIMESTAMP),
('prb-07', 'cat-14', 'Water Pipe Leakage / Seepage', 'Concealed or exposed water pipe leaking, causing seepage or pressure loss', 600.00, CURRENT_TIMESTAMP),
('prb-08', 'cat-14', 'Tap / Faucet Replacement or Jamming', 'Dripping tap, broken ceramic disc or low water pressure from faucet', 350.00, CURRENT_TIMESTAMP),
('prb-09', 'cat-13', 'Switchboard Sparking / Burnt Socket', 'Loose electrical contacts, burning smell or non-functional power socket', 400.00, CURRENT_TIMESTAMP),
('prb-10', 'cat-15', 'Door Lock / Latch Stuck or Broken', 'Key jammed, mortise lock replacement or latch alignment issue', 550.00, CURRENT_TIMESTAMP);

-- 6. SEED USERS (Password: password123)
-- BCrypt: $2a$10$7EqJtq98hPqEX7fNZaFWoO.8H1/4t2Vspv8.a3F/z7D1b5eP5W7zK
INSERT INTO users (id, email, password_hash, full_name, phone_number, role, is_active, created_at, updated_at) VALUES
('usr-admin', 'admin@repairmatch.com', '$2a$10$K6802WmKSXx.Lkd1fEw2nORIpER5XEHkfAif/A7eNRhMV5F3ftl2K', 'System Administrator', '+91 9999900001', 'ADMIN', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('usr-cust-1', 'rahul@gmail.com', '$2a$10$K6802WmKSXx.Lkd1fEw2nORIpER5XEHkfAif/A7eNRhMV5F3ftl2K', 'Rahul Verma', '+91 9876543210', 'CUSTOMER', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('usr-cust-2', 'priya@gmail.com', '$2a$10$K6802WmKSXx.Lkd1fEw2nORIpER5XEHkfAif/A7eNRhMV5F3ftl2K', 'Priya Sharma', '+91 9876543211', 'CUSTOMER', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('usr-tech-1', 'rajesh.tech@repairmatch.com', '$2a$10$K6802WmKSXx.Lkd1fEw2nORIpER5XEHkfAif/A7eNRhMV5F3ftl2K', 'Rajesh Kumar (Tech Specialist)', '+91 9811122233', 'TECHNICIAN', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('usr-tech-2', 'amit.tech@repairmatch.com', '$2a$10$K6802WmKSXx.Lkd1fEw2nORIpER5XEHkfAif/A7eNRhMV5F3ftl2K', 'Amit Singh (Appliance Master)', '+91 9822233344', 'TECHNICIAN', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('usr-tech-3', 'vikram.plumber@repairmatch.com', '$2a$10$K6802WmKSXx.Lkd1fEw2nORIpER5XEHkfAif/A7eNRhMV5F3ftl2K', 'Vikram Patel (Expert Plumber)', '+91 9833344455', 'TECHNICIAN', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('usr-tech-4', 'suresh.electric@repairmatch.com', '$2a$10$K6802WmKSXx.Lkd1fEw2nORIpER5XEHkfAif/A7eNRhMV5F3ftl2K', 'Suresh Nair (Licensed Electrician)', '+91 9844455566', 'TECHNICIAN', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('usr-tech-5', 'new.tech@repairmatch.com', '$2a$10$K6802WmKSXx.Lkd1fEw2nORIpER5XEHkfAif/A7eNRhMV5F3ftl2K', 'Mohan Lal (Unverified Tech)', '+91 9855566677', 'TECHNICIAN', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 7. SEED ADDRESSES
INSERT INTO addresses (id, user_id, street, city, state, postal_code, latitude, longitude, is_default, created_at) VALUES
('addr-c1', 'usr-cust-1', 'Flat 402, Green Glen Layout, Bellandur', 'Bengaluru', 'Karnataka', '560103', 12.9298, 77.6748, TRUE, CURRENT_TIMESTAMP),
('addr-c2', 'usr-cust-2', '12th Main, HAL 2nd Stage, Indiranagar', 'Bengaluru', 'Karnataka', '560038', 12.9719, 77.6412, TRUE, CURRENT_TIMESTAMP);

-- 8. SEED TECHNICIAN PROFILES
INSERT INTO technician_profiles (id, user_id, bio, experience_years, verification_status, kyc_document_url, base_inspection_fee, service_radius_km, latitude, longitude, average_rating, total_reviews, completed_jobs_count, is_available, created_at, updated_at) VALUES
('tech-prof-1', 'usr-tech-1', 'Apple & Dell certified hardware engineer with 8+ years experience in chip-level logic board and display repair.', 8, 'VERIFIED', 'https://docs.repairmatch.com/kyc/tech-1.pdf', 299.00, 15.0, 12.9716, 77.6413, 4.9, 48, 126, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('tech-prof-2', 'usr-tech-2', 'Senior refrigeration & HVAC technician specializing in Daikin, LG and Samsung inverters and cooling cycles.', 6, 'VERIFIED', 'https://docs.repairmatch.com/kyc/tech-2.pdf', 249.00, 12.0, 12.9352, 77.6245, 4.7, 32, 94, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('tech-prof-3', 'usr-tech-3', 'Licensed master plumber handling residential pipelines, bathroom fittings, motor pumps and concealed water leakage.', 10, 'VERIFIED', 'https://docs.repairmatch.com/kyc/tech-3.pdf', 199.00, 20.0, 12.9784, 77.6408, 4.8, 56, 180, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('tech-prof-4', 'usr-tech-4', 'Certified industrial & domestic wireman. Expertise in short circuit detection, MCB upgrades and inverter setups.', 5, 'VERIFIED', 'https://docs.repairmatch.com/kyc/tech-4.pdf', 199.00, 10.0, 12.9279, 77.6271, 4.6, 18, 52, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('tech-prof-5', 'usr-tech-5', 'Junior technician with general handyman and electronics knowledge awaiting document approval.', 1, 'PENDING', 'https://docs.repairmatch.com/kyc/tech-5.pdf', 149.00, 8.0, 12.9500, 77.6000, 0.0, 0, 0, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 9. SEED TECHNICIAN SKILLS (Categories, Brands, Problems)
-- Tech 1: Smartphones, Laptops, Tablets | Brands: Apple, Dell, Samsung | Problems: Screen, Battery, Display, Motherboard
INSERT INTO technician_categories (technician_id, category_id) VALUES
('tech-prof-1', 'cat-01'),
('tech-prof-1', 'cat-02'),
('tech-prof-1', 'cat-03');

INSERT INTO technician_brands (technician_id, brand_id) VALUES
('tech-prof-1', 'brd-01'),
('tech-prof-1', 'brd-02'),
('tech-prof-1', 'brd-05');

INSERT INTO technician_problems (technician_id, problem_type_id) VALUES
('tech-prof-1', 'prb-01'),
('tech-prof-1', 'prb-02'),
('tech-prof-1', 'prb-03'),
('tech-prof-1', 'prb-04');

-- Tech 2: ACs, Refrigerators, Washing Machines | Brands: Daikin, LG, Samsung, Voltas
INSERT INTO technician_categories (technician_id, category_id) VALUES
('tech-prof-2', 'cat-05'),
('tech-prof-2', 'cat-06'),
('tech-prof-2', 'cat-07');

INSERT INTO technician_brands (technician_id, brand_id) VALUES
('tech-prof-2', 'brd-02'),
('tech-prof-2', 'brd-08'),
('tech-prof-2', 'brd-10'),
('tech-prof-2', 'brd-11');

INSERT INTO technician_problems (technician_id, problem_type_id) VALUES
('tech-prof-2', 'prb-05'),
('tech-prof-2', 'prb-06');

-- Tech 3: Plumbing
INSERT INTO technician_categories (technician_id, category_id) VALUES
('tech-prof-3', 'cat-14');

INSERT INTO technician_problems (technician_id, problem_type_id) VALUES
('tech-prof-3', 'prb-07'),
('tech-prof-3', 'prb-08');

-- Tech 4: Electrical Repair & Inverters
INSERT INTO technician_categories (technician_id, category_id) VALUES
('tech-prof-4', 'cat-12'),
('tech-prof-4', 'cat-13');

INSERT INTO technician_problems (technician_id, problem_type_id) VALUES
('tech-prof-4', 'prb-09');

-- 10. SEED BOOKINGS (Demonstrating different states)
INSERT INTO bookings (id, booking_reference, customer_id, technician_id, category_id, brand_id, model_id, problem_type_id, problem_description, address_id, scheduled_date, time_slot, status, inspection_fee, final_amount, payment_status, cancellation_reason, created_at, updated_at) VALUES
('bk-001', 'RM-2026-0001', 'usr-cust-2', 'tech-prof-1', 'cat-01', 'brd-01', 'mod-01', 'prb-01', 'Screen cracked after accidental drop from table. Glass shattered but OLED displays faintly.', 'addr-c2', '2026-09-18', '10:00 AM - 01:00 PM', 'COMPLETED', 299.00, 2799.00, 'PAID', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bk-002', 'RM-2026-0002', 'usr-cust-1', 'tech-prof-2', 'cat-05', 'brd-10', 'mod-07', 'prb-05', 'AC blowing room temperature air instead of cool air. Filter cleaned already.', 'addr-c1', '2026-09-22', '02:00 PM - 05:00 PM', 'ACCEPTED', 249.00, NULL, 'PENDING', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bk-003', 'RM-2026-0003', 'usr-cust-1', 'tech-prof-3', 'cat-14', NULL, NULL, 'prb-07', 'Kitchen sink drain pipe leaking water into under-counter cabinet.', 'addr-c1', '2026-09-23', '09:00 AM - 12:00 PM', 'PENDING', 199.00, NULL, 'PENDING', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 11. SEED BOOKING TIMELINE
INSERT INTO booking_timeline (id, booking_id, status, remarks, created_at) VALUES
('bt-01', 'bk-001', 'PENDING', 'Booking request initiated by customer', CURRENT_TIMESTAMP),
('bt-02', 'bk-001', 'ACCEPTED', 'Technician Rajesh Kumar accepted the service request', CURRENT_TIMESTAMP),
('bt-03', 'bk-001', 'IN_PROGRESS', 'Technician arrived at customer premise and began display replacement', CURRENT_TIMESTAMP),
('bt-04', 'bk-001', 'COMPLETED', 'Original OLED display fitted and tested. Customer approved test.', CURRENT_TIMESTAMP),
('bt-05', 'bk-002', 'PENDING', 'Booking request created', CURRENT_TIMESTAMP),
('bt-06', 'bk-002', 'ACCEPTED', 'Technician Amit Singh confirmed appointment for 2026-09-22', CURRENT_TIMESTAMP),
('bt-07', 'bk-003', 'PENDING', 'Awaiting technician confirmation', CURRENT_TIMESTAMP);

-- 12. SEED REVIEWS
INSERT INTO reviews (id, booking_id, customer_id, technician_id, rating, comment, created_at) VALUES
('rev-001', 'bk-001', 'usr-cust-2', 'tech-prof-1', 5, 'Rajesh did an exceptional job! He replaced my iPhone 14 Pro screen in under 45 minutes with genuine parts. Very polite and professional.', CURRENT_TIMESTAMP);
