DROP TABLE IF EXISTS payments CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS rooms CASCADE;
DROP TABLE IF EXISTS hotels CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS room_type CASCADE;

CREATE TABLE hotels (
    hotelId BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200),
    description TEXT,
    address VARCHAR(500),
    city VARCHAR(100),
    zipCode VARCHAR(30),
    phone VARCHAR(30),
    email VARCHAR(255),
    checkIn TIME,
    checkOut TIME,
    starRate INT,
    isActive BOOLEAN,
    latitude VARCHAR(30),
    longitude VARCHAR(30),
    type INT,
    created_at TIMESTAMP NULL,
    modified_at TIMESTAMP NULL
);

CREATE TABLE rooms (
    room_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(50),
    floor INT,
    room_status INT,
    name VARCHAR(100),
    size INT,
    base_price NUMERIC(12, 2),
    max_adult INT,
    max_child INT,
    is_active BOOLEAN,
    hotel_id BIGINT,
    room_type INT,
    room_view_option INT,
    room_bed_option INT,
    created_at TIMESTAMP NULL,
    modified_at TIMESTAMP NULL
);

CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255),
    password VARCHAR(255),
    name VARCHAR(100),
    phone VARCHAR(30),
    role VARCHAR(50),
    status VARCHAR(50),
    Membership VARCHAR(50),
    socialLogin VARCHAR(10),
    socialLoginId VARCHAR(30),
    created_at TIMESTAMP NULL,
    modified_at TIMESTAMP NULL
);

CREATE TABLE bookings (
    booking_id BIGINT PRIMARY KEY,
    user_id BIGINT,
    hotel_id BIGINT,
    room_id BIGINT,
    channel_id BIGINT,
    booking_no VARCHAR(50),
    guest_last_name VARCHAR(50),
    guest_first_name VARCHAR(50),
    guest_phone VARCHAR(30),
    guest_email VARCHAR(255),
    nationality VARCHAR(10),
    nights INT,
    guest_count INT,
    check_in_date DATE,
    check_in_time TIME,
    check_out_date DATE,
    check_out_time TIME,
    cancelled_at TIMESTAMP
);

CREATE TABLE payments (
    payment_id BIGINT PRIMARY KEY,
    booking_id BIGINT,
    payment_method VARCHAR(30),
    payment_status VARCHAR(30),
    amount NUMERIC(12, 2),
    currency CHAR(3),
    paid_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    cardCompany VARCHAR(100),
    cardLast4 VARCHAR(4)
);

CREATE TABLE channels (
    채널ID BIGINT PRIMARY KEY,
    채널명 VARCHAR(100),
    채널유형 VARCHAR(20),
    수수료율 NUMERIC(5, 2),
    활성여부 BOOLEAN,
    생성시간 TIMESTAMP
);

CREATE TABLE room_type (
    room_type_id BIGINT PRIMARY KEY,
    name VARCHAR(100)
);
