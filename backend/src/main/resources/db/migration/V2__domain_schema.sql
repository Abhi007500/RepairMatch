-- V2__domain_schema.sql
-- Complete Relational Domain Schema for RepairMatch

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

CREATE TABLE IF NOT EXISTS addresses (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    is_default BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_addresses_user ON addresses(user_id);

CREATE TABLE IF NOT EXISTS technician_profiles (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL UNIQUE,
    bio VARCHAR(1000),
    experience_years INT DEFAULT 0 NOT NULL,
    verification_status VARCHAR(20) DEFAULT 'PENDING' NOT NULL,
    kyc_document_url VARCHAR(255),
    base_inspection_fee NUMERIC(10, 2) DEFAULT 150.00 NOT NULL,
    service_radius_km DOUBLE PRECISION DEFAULT 15.0 NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    average_rating DOUBLE PRECISION DEFAULT 0.0 NOT NULL,
    total_reviews INT DEFAULT 0 NOT NULL,
    completed_jobs_count INT DEFAULT 0 NOT NULL,
    is_available BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_tech_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_tech_verification ON technician_profiles(verification_status);
CREATE INDEX idx_tech_availability ON technician_profiles(is_available);

CREATE TABLE IF NOT EXISTS categories (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    icon_name VARCHAR(50),
    requires_brand_and_model BOOLEAN DEFAULT FALSE NOT NULL,
    display_order INT DEFAULT 0 NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_categories_slug ON categories(slug);

CREATE TABLE IF NOT EXISTS brands (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS category_brands (
    category_id VARCHAR(36) NOT NULL,
    brand_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (category_id, brand_id),
    CONSTRAINT fk_cb_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    CONSTRAINT fk_cb_brand FOREIGN KEY (brand_id) REFERENCES brands(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS models (
    id VARCHAR(36) PRIMARY KEY,
    brand_id VARCHAR(36) NOT NULL,
    category_id VARCHAR(36) NOT NULL,
    name VARCHAR(150) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_models_brand FOREIGN KEY (brand_id) REFERENCES brands(id) ON DELETE CASCADE,
    CONSTRAINT fk_models_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

CREATE INDEX idx_models_brand_cat ON models(brand_id, category_id);

CREATE TABLE IF NOT EXISTS problem_types (
    id VARCHAR(36) PRIMARY KEY,
    category_id VARCHAR(36) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    typical_price_estimate NUMERIC(10, 2) DEFAULT 0.00 NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_problem_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

CREATE INDEX idx_problems_category ON problem_types(category_id);

CREATE TABLE IF NOT EXISTS technician_categories (
    technician_id VARCHAR(36) NOT NULL,
    category_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (technician_id, category_id),
    CONSTRAINT fk_tc_tech FOREIGN KEY (technician_id) REFERENCES technician_profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_tc_cat FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS technician_brands (
    technician_id VARCHAR(36) NOT NULL,
    brand_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (technician_id, brand_id),
    CONSTRAINT fk_tb_tech FOREIGN KEY (technician_id) REFERENCES technician_profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_tb_brand FOREIGN KEY (brand_id) REFERENCES brands(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS technician_problems (
    technician_id VARCHAR(36) NOT NULL,
    problem_type_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (technician_id, problem_type_id),
    CONSTRAINT fk_tp_tech FOREIGN KEY (technician_id) REFERENCES technician_profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_tp_prob FOREIGN KEY (problem_type_id) REFERENCES problem_types(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
    id VARCHAR(36) PRIMARY KEY,
    booking_reference VARCHAR(50) NOT NULL UNIQUE,
    customer_id VARCHAR(36) NOT NULL,
    technician_id VARCHAR(36) NOT NULL,
    category_id VARCHAR(36) NOT NULL,
    brand_id VARCHAR(36),
    model_id VARCHAR(36),
    problem_type_id VARCHAR(36),
    problem_description VARCHAR(1000) NOT NULL,
    address_id VARCHAR(36) NOT NULL,
    scheduled_date VARCHAR(20) NOT NULL,
    time_slot VARCHAR(50) NOT NULL,
    status VARCHAR(30) DEFAULT 'PENDING' NOT NULL,
    inspection_fee NUMERIC(10, 2) NOT NULL,
    final_amount NUMERIC(10, 2),
    payment_status VARCHAR(30) DEFAULT 'PENDING' NOT NULL,
    cancellation_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_booking_customer FOREIGN KEY (customer_id) REFERENCES users(id),
    CONSTRAINT fk_booking_tech FOREIGN KEY (technician_id) REFERENCES technician_profiles(id),
    CONSTRAINT fk_booking_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_booking_address FOREIGN KEY (address_id) REFERENCES addresses(id)
);

CREATE INDEX idx_bookings_customer ON bookings(customer_id);
CREATE INDEX idx_bookings_tech ON bookings(technician_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_date ON bookings(scheduled_date);

CREATE TABLE IF NOT EXISTS booking_timeline (
    id VARCHAR(36) PRIMARY KEY,
    booking_id VARCHAR(36) NOT NULL,
    status VARCHAR(30) NOT NULL,
    remarks VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_timeline_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
);

CREATE INDEX idx_timeline_booking ON booking_timeline(booking_id);

CREATE TABLE IF NOT EXISTS reviews (
    id VARCHAR(36) PRIMARY KEY,
    booking_id VARCHAR(36) NOT NULL UNIQUE,
    customer_id VARCHAR(36) NOT NULL,
    technician_id VARCHAR(36) NOT NULL,
    rating INT NOT NULL,
    comment VARCHAR(1000),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_review_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_customer FOREIGN KEY (customer_id) REFERENCES users(id),
    CONSTRAINT fk_review_tech FOREIGN KEY (technician_id) REFERENCES technician_profiles(id)
);

CREATE INDEX idx_reviews_tech ON reviews(technician_id);
