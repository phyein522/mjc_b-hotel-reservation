CREATE TABLE `payments` (
	`payment_id`	BIGINT	NOT NULL,
	`booking_id`	BIGINT	NOT NULL,
	`payment_method`	VARCHAR(30)	NOT NULL,
	`payment_status`	ENUM( 'READY', 'PAID', 'FAILED', 'CANCELLED', 'PARTIAL_REFUNDED', 'REFUNDED' )	NOT NULL	DEFAULT 'READY',
	`total_amount`	NUMERIC(12, 2)	NOT NULL,
	`currency`	CHAR(3)	NOT NULL	DEFAULT 'KRW',
	`paid_at`	TIMESTAMP	NOT NULL,
	`cancelled_at`	TIMESTAMP	NULL,
	`card_company`	VARCHAR(100)	NULL,
	`card_last4`	VARCHAR(4)	NULL,
	`used_point`	INT	NULL,
	`discount_amount`	NUMERIC(12,2)	NULL,
	`pg_transaction_id`	VARCHAR(100)	NULL
);

CREATE TABLE `check_trip_type` (
	`여행유형선택`	BIGINT	NOT NULL,
	`여행유형아이디`	BIGINT	NULL,
	`선택유뮤`	BOOLEAN	NULL
);

CREATE TABLE `reviews` (
	`리뷰_id`	BIGINT	NULL,
	`예약_id`	BIGINT	NULL,
	`user_id`	BIGINT	NULL,
	`호텔_id`	BIGINT	NULL,
	`여행유형선택`	BIGINT	NOT NULL,
	`조회수`	SMALLINT	NOT NULL,
	`제목`	VARCHAR(200)	NULL,
	`내용`	TEXT	NULL,
	`생성 시간`	TIMESTAMP	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	`수정 시간`	TIMESTAMP	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	`전체평점`	BIGINT	NULL,
	`좋아요`	BIGINT	NULL	DEFAULT 0,
	`싫어요`	BIGINT	NULL	DEFAULT 0
);

CREATE TABLE `reviews_tags` (
	`태그id`	BIGINT	NULL,
	`리뷰_id`	BIGINT	NULL,
	`태그_id`	BIGINT	NULL,
	`상태`	BOOLEAN	NULL
);

CREATE TABLE `trip_type_` (
	`여행유형아이디`	BIGINT	NULL,
	`유형이름`	VARCHAR(200)	NULL
);

CREATE TABLE `room_images` (
	`room_image_id`	BIGINT	NOT NULL,
	`file_name`	VARCHAR(100)	NOT NULL,
	`path`	VARCHAR(100)	NOT NULL	DEFAULT 0,
	`size`	INT	NOT NULL	DEFAULT FALSE,
	`store_name`	VARCHAR(100)	NULL,
	`ext`	VARCHAR(10)	NULL,
	`room_id`	BIGINT	NOT NULL
);

CREATE TABLE `checkin_qr_codes` (
	`QR_ID`	BIGINT	NOT NULL,
	`user_id`	BIGINT	NOT NULL,
	`qr_token`	VARCHAR(128)	NOT NULL,
	`qr_payload`	TEXT	NOT NULL,
	`status`	VARCHAR(20)	NOT NULL	DEFAULT 'PENDING',
	`available_from`	TIMESTAMPTZ	NOT NULL,
	`available_until`	TIMESTAMPTZ	NOT NULL,
	`used_at`	TIMESTAMPTZ	NULL,
	`revoked_at`	TIMESTAMPTZ	NULL,
	`revoke_reason`	VARCHAR(100)	NULL,
	`created_at`	TIMESTAMPTZ	NOT NULL	DEFAULT now(),
	`updated_at`	TIMESTAMPTZ	NOT NULL	DEFAULT now()
);

CREATE TABLE `qr_code_statuses` (
	`code`	BIGINT	NOT NULL,
	`name`	VARCHAR(50)	NOT NULL,
	`description`	VARCHAR(255)	NULL
);

CREATE TABLE `hotel_amenities` (
	`amen_id`	BIGINT	NOT NULL,
	`hotel_id`	BIGSERIAL	NOT NULL,
	`free_wifi`	BOOLEAN	NULL,
	`pool`	BOOLEAN	NULL,
	`fitness_center`	BOOLEAN	NULL,
	`spa`	BOOLEAN	NULL,
	`restaurant`	BOOLEAN	NULL,
	`valet_parking`	BOOLEAN	NULL,
	`free_parking`	BOOLEAN	NOT NULL,
	`concierge`	BOOLEAN	NOT NULL,
	`bar`	BOOLEAN	NULL,
	`breakfast`	BOOLEAN	NOT NULL,
	`airport_shuttle`	BOOLEAN	NOT NULL,
	`room_service`	BOOLEAN	NOT NULL,
	`laundry`	BOOLEAN	NOT NULL,
	`lounge`	BOOLEAN	NOT NULL,
	`sauna`	BOOLEAN	NOT NULL,
	`free_cancel`	BOOLEAN	NOT NULL,
	`pet_friendly`	BOOLEAN	NOT NULL
);

CREATE TABLE `refunds` (
	`refund_id`	BIGINT	NOT NULL,
	`payment_id`	BIGINT	NOT NULL,
	`refund_amount`	NUMERIC(12, 2)	NOT NULL,
	`reason`	TEXT	NULL,
	`refund_status`	VARCHAR(30)	NOT NULL	DEFAULT 'REQUESTED',
	`refunded_at`	TIMESTAMP	NULL,
	`created_at`	TIMESTAMP	NOT NULL	DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `transportaion` (
	`trans_id`	BIGINT	NOT NULL,
	`hotel_id`	BIGINT	NOT NULL,
	`transname`	VARCHAR(200)	NULL,
	`transtime`	String	NULL,
	`transdepart`	VARCHAR(100)	NULL
);

CREATE TABLE `wishlists` (
	`user_id`	BIGINT	NOT NULL,
	`hotel_id`	BIGINT	NOT NULL,
	`created_at`	TIMESTAMP	NOT NULL	DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `rooms` (
	`room_id`	BIGINT	NOT NULL,
	`room_number`	VARCHAR(50)	NOT NULL,
	`floor`	INT	NULL,
	`status`	INT	NOT NULL	COMMENT '예약가능/예약불가/공사중',
	`created_at`	TIMESTAMP	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	`updated_at`	TIMESTAMP	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	`name`	VARCHAR(100)	NULL,
	`size`	INT	NULL,
	`base_price`	NUMERIC(12,2)	NULL,
	`max_adult`	INT	NULL,
	`max_children`	INT	NULL,
	`is_active`	BOOLEAN	NULL,
	`호텔아이디`	BIGSERIAL	NOT NULL,
	`bed_option`	INT	NULL	COMMENT '온돌/더블침대/퀸침대',
	`view_option`	INT	NULL	COMMENT '한강뷰, 도심뷰 등',
	`room_type`	INT	NULL	COMMENT '스위트, 디럭스, 스탠다드 등',
	`block_start_date`	DATE	NULL,
	`bloack_end_date`	DATE	NULL,
	`description`	TEXT NULL	NULL
);

CREATE TABLE `qr_code_scan_logs` (
	`QRLOG_ID`	BIGINT	NOT NULL,
	`QR_ID`	BIGINT	NOT NULL,
	`scanned_at`	TIMESTAMPTZ	NOT NULL	DEFAULT now(),
	`scan_result`	VARCHAR(20)	NOT NULL,
	`scanned_by`	VARCHAR(100)	NULL,
	`device_info`	VARCHAR(255)	NULL,
	`ip_address`	VARCHAR(45)	NULL
);

CREATE TABLE `bookings` (
	`booking_id`	BIGINT	NOT NULL,
	`user_id`	BIGINT	NULL,
	`room_id`	BIGINT	NOT NULL,
	`booking_no`	VARCHAR(50)	NOT NULL,
	`guest_name`	VARCHAR(100)	NOT NULL,
	`nationality`	ENUM('KOREA', 'US', 'CHINA', 'JAPAN', 'TAIWAN', 'HONG_KONG', 'PHILIPPINES', 'VIETNAM', 'SINGAPORE', 'OTHER')	NOT NULL	DEFAULT 'PENDING',
	`guest_phone`	VARCHAR(30)	NOT NULL,
	`guest_email`	VARCHAR(255)	NOT NULL,
	`special_request`	VARCHAR(255)	NULL,
	`nights`	INT	NULL,
	`adult_count`	INT	NULL,
	`child_count`	INT	NULL,
	`checkin_date`	DATE	NOT NULL,
	`checkin_time`	TIME	NOT NULL,
	`checkout_date`	DATE	NOT NULL,
	`checkout_time`	TIME	NOT NULL,
	`cancelled_at`	TIMESTAMP	NULL
);

CREATE TABLE `rate_policies` (
	`policy_id`	BIGINT	NOT NULL	COMMENT '정책ID - PK',
	`최소 투숙 박수`	INT	NOT NULL	DEFAULT 1	COMMENT '최소 투숙 기간 (박수)',
	`체크인 시간`	TIME	NOT NULL	DEFAULT '15:00'	COMMENT '체크인 기준 시간',
	`체크아웃 시간`	TIME	NOT NULL	DEFAULT '11:00'	COMMENT '체크아웃 기준 시간',
	`취소 기한(일)`	INT	NOT NULL	DEFAULT 3	COMMENT '취소 가능 기한 (체크인 기준 N일 전)',
	`취소 수수료율`	NUMERIC(5, 2)	NOT NULL	DEFAULT 0.00	COMMENT '취소 수수료율 (%) - 기한 초과시 적용',
	`무료 아동 나이`	INT	NOT NULL	DEFAULT 12	COMMENT '무료 아동 나이 기준 (N세 이하 무료)',
	`created_at`	TIMESTAMP	NOT NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '등록일시',
	`Field`	INT	NULL,
	`child_rate_type`	ENUM('FREE','DISCOUNT')	NOT NULL	DEFAULT 'FREE'	NULL,
	`child_discount_rate`	NUMERIC(5,2)	NULL	NULL,
	`hotel_id`	BIGSERIAL	NOT NULL
);

CREATE TABLE `hotel_images` (
	`hotel_image_id`	BIGINT	NOT NULL,
	`hotel_id`	BIGINT	NOT NULL,
	`filename`	String	NOT NULL,
	`size`	INT	NOT NULL	DEFAULT 0,
	`ext`	String	NOT NULL	DEFAULT FALSE,
	`storeName`	String	NULL,
	`path`	String	NULL
);

CREATE TABLE `rating_categories` (
	`카테고리id`	BIGINT	NULL,
	`평점이름`	VARCHAR(100)	NULL
);

CREATE TABLE `room_amenities` (
	`room_amenity_id`	BIGINT	NOT NULL,
	`wifi`	BOOLEAN	NOT NULL	DEFAULT FALSE,
	`tv`	BOOLEAN	NOT NULL	DEFAULT FALSE,
	`bathtub`	BOOLEAN	NOT NULL	DEFAULT FALSE,
	`city_view`	BOOLEAN	NOT NULL	DEFAULT FALSE,
	`ocean_view`	BOOLEAN	NOT NULL	DEFAULT FALSE,
	`breakfast_included`	BOOLEAN	NOT NULL	DEFAULT FALSE,
	`non_smoking`	BOOLEAN	NOT NULL	DEFAULT FALSE,
	`room_id`	BIGINT	NOT NULL
);

CREATE TABLE `hotels` (
	`hotel_id`	BIGSERIAL	NOT NULL,
	`user_id`	BIGINT	NOT NULL,
	`name`	VARCHAR(200)	NOT NULL,
	`description`	TEXT	NULL,
	`address`	VARCHAR(500)	NOT NULL,
	`city`	VARCHAR(100)	NOT NULL,
	`zipCode`	VARCHAR(30)	NULL,
	`phone`	VARCHAR(30)	NULL,
	`email`	VARCHAR(255)	NULL,
	`checkIn`	TIME	NOT NULL	DEFAULT '15:00',
	`checkOut`	TIME	NOT NULL	DEFAULT '11:00',
	`starRate`	SMALLINT	NULL,
	`isActive`	BOOLEAN	NOT NULL	DEFAULT TRUE,
	`latitude`	DECIMAL(10,7)	NULL,
	`longitude`	DECIMAL(10,7)	NULL,
	`type`	INT	NULL
);

CREATE TABLE `tag_type` (
	`태그_id`	BIGINT	NULL,
	`태그 이름`	VARCHAR(200)	NULL,
	`태그타입(good/bad)`	BOOLEAN	NULL
);

CREATE TABLE `reivews_rating` (
	`id`	BIGINT	NULL,
	`리뷰_id`	BIGINT	NULL,
	`카테고리id`	BIGINT	NULL,
	`점수`	BIGINT	NULL
);

CREATE TABLE `users` (
	`user_id`	BIGINT	NOT NULL,
	`email`	VARCHAR(255)	NOT NULL,
	`password`	VARCHAR(255)	NOT NULL,
	`name`	VARCHAR(100)	NOT NULL,
	`phone`	VARCHAR(30)	NULL,
	`role`	ENUM('CUSTOMER', 'HOTEL_MANAGER', 'ADMIN', 'SUPER_ADMIN')	NOT NULL	DEFAULT 'CUSTOMER',
	`status`	ENUM('ACTIVE', 'DORMANT', 'LOCKED', 'WITHDRAWN')	NOT NULL	DEFAULT TRUE	COMMENT '활성화, 휴면, 잠김, 탈퇴',
	`membership`	ENUM('NEW_Member', 'STANDARD', 'GOLD', 'VIP', 'VVIP'))	NULL,
	`marketing_agreed`	BOOLEAN	NOT NULL
);

CREATE TABLE `reviews_photo` (
	`id`	BIGINT	NOT NULL,
	`리뷰_id`	BIGINT	NOT NULL,
	`사진경로`	VARCAHR(500)	NOT NULL,
	`사진순서`	BIGINT	NULL
);

ALTER TABLE `payments` ADD CONSTRAINT `PK_PAYMENTS` PRIMARY KEY (
	`payment_id`
);

ALTER TABLE `check_trip_type` ADD CONSTRAINT `PK_CHECK_TRIP_TYPE` PRIMARY KEY (
	`여행유형선택`
);

ALTER TABLE `reviews` ADD CONSTRAINT `PK_REVIEWS` PRIMARY KEY (
	`리뷰_id`
);

ALTER TABLE `reviews_tags` ADD CONSTRAINT `PK_REVIEWS_TAGS` PRIMARY KEY (
	`태그id`
);

ALTER TABLE `trip_type_` ADD CONSTRAINT `PK_TRIP_TYPE_` PRIMARY KEY (
	`여행유형아이디`
);

ALTER TABLE `room_images` ADD CONSTRAINT `PK_ROOM_IMAGES` PRIMARY KEY (
	`room_image_id`
);

ALTER TABLE `checkin_qr_codes` ADD CONSTRAINT `PK_CHECKIN_QR_CODES` PRIMARY KEY (
	`QR_ID`
);

ALTER TABLE `qr_code_statuses` ADD CONSTRAINT `PK_QR_CODE_STATUSES` PRIMARY KEY (
	`code`
);

ALTER TABLE `hotel_amenities` ADD CONSTRAINT `PK_HOTEL_AMENITIES` PRIMARY KEY (
	`amen_id`
);

ALTER TABLE `refunds` ADD CONSTRAINT `PK_REFUNDS` PRIMARY KEY (
	`refund_id`
);

ALTER TABLE `transportaion` ADD CONSTRAINT `PK_TRANSPORTAION` PRIMARY KEY (
	`trans_id`
);

ALTER TABLE `wishlists` ADD CONSTRAINT `PK_WISHLISTS` PRIMARY KEY (
	`user_id`,
	`hotel_id`
);

ALTER TABLE `rooms` ADD CONSTRAINT `PK_ROOMS` PRIMARY KEY (
	`room_id`
);

ALTER TABLE `qr_code_scan_logs` ADD CONSTRAINT `PK_QR_CODE_SCAN_LOGS` PRIMARY KEY (
	`QRLOG_ID`
);

ALTER TABLE `bookings` ADD CONSTRAINT `PK_BOOKINGS` PRIMARY KEY (
	`booking_id`
);

ALTER TABLE `rate_policies` ADD CONSTRAINT `PK_RATE_POLICIES` PRIMARY KEY (
	`policy_id`
);

ALTER TABLE `hotel_images` ADD CONSTRAINT `PK_HOTEL_IMAGES` PRIMARY KEY (
	`hotel_image_id`
);

ALTER TABLE `rating_categories` ADD CONSTRAINT `PK_RATING_CATEGORIES` PRIMARY KEY (
	`카테고리id`
);

ALTER TABLE `room_amenities` ADD CONSTRAINT `PK_ROOM_AMENITIES` PRIMARY KEY (
	`room_amenity_id`
);

ALTER TABLE `hotels` ADD CONSTRAINT `PK_HOTELS` PRIMARY KEY (
	`hotel_id`
);

ALTER TABLE `tag_type` ADD CONSTRAINT `PK_TAG_TYPE` PRIMARY KEY (
	`태그_id`
);

ALTER TABLE `reivews_rating` ADD CONSTRAINT `PK_REIVEWS_RATING` PRIMARY KEY (
	`id`
);

ALTER TABLE `users` ADD CONSTRAINT `PK_USERS` PRIMARY KEY (
	`user_id`
);

ALTER TABLE `reviews_photo` ADD CONSTRAINT `PK_REVIEWS_PHOTO` PRIMARY KEY (
	`id`
);

ALTER TABLE `wishlists` ADD CONSTRAINT `FK_users_TO_wishlists_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `users` (
	`user_id`
);

ALTER TABLE `wishlists` ADD CONSTRAINT `FK_hotels_TO_wishlists_1` FOREIGN KEY (
	`hotel_id`
)
REFERENCES `hotels` (
	`hotel_id`
);

