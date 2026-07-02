CREATE TABLE `payments` (
	`payment_id`	BIGINT	NOT NULL,
	`booking_id`	BIGINT	NOT NULL,
	`payment_method`	VARCHAR(30)	NOT NULL,
	`payment_status`	ENUM( 'READY', 'PAID', 'FAILED', 'CANCELLED', 'PARTIAL_REFUNDED', 'REFUNDED' )	NOT NULL	DEFAULT 'READY',
	`amount`	NUMERIC(12, 2)	NOT NULL,
	`currency`	CHAR(3)	NOT NULL	DEFAULT 'KRW',
	`paid_at`	TIMESTAMP	NOT NULL,
	`cancelled_at`	TIMESTAMP	NULL,
	`card_company`	VARCHAR(100)	NULL,
	`card_last4`	VARCHAR(4)	NULL
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

CREATE TABLE `promotionsale` (
	`promotion_sale_id`	BIGINT	NOT NULL,
	`promotion_id`	BIGINT	NOT NULL,
	`promotion_sale_context`	TEXT	NULL
);

CREATE TABLE `point_transaction_types` (
	`code`	VARCHAR(20)	NOT NULL,
	`name`	VARCHAR(50)	NOT NULL,
	`direction`	SMALLINT	NOT NULL
);

CREATE TABLE `trip_type_` (
	`여행유형아이디`	BIGINT	NULL,
	`유형이름`	VARCHAR(200)	NULL
);

CREATE TABLE `room_images` (
	`room_image_id`	BIGINT	NOT NULL,
	`image_url`	TEXT	NOT NULL,
	`sort_order`	INT	NOT NULL	DEFAULT 0,
	`is_thumbnail`	BOOLEAN	NOT NULL	DEFAULT FALSE,
	`room_id`	BIGINT	NOT NULL
);

CREATE TABLE `membership_grade_histories` (
	`id`	BIGSERIAL	NOT NULL,
	`user_id`	BIGINT	NOT NULL,
	`from_grade_id`	BIGINT	NULL,
	`to_grade_id`	BIGINT	NOT NULL,
	`change_reason`	VARCHAR(50)	NOT NULL,
	`triggered_by_reservation_id`	BIGINT	NULL,
	`note`	TEXT	NULL,
	`changed_at`	TIMESTAMPTZ	NOT NULL	DEFAULT now()
);

CREATE TABLE `channels` (
	`채널ID`	BIGINT	NOT NULL	COMMENT '채널ID - PK',
	`채널명`	VARCHAR(100)	NOT NULL	COMMENT '채널명 (예: 야놀자, 익스피디아, 직접예약)',
	`채널유형`	VARCHAR(20)	NOT NULL	COMMENT '채널유형 - DIRECT(직접)/OTA(온라인여행사)/AGENCY(여행사)',
	`수수료율`	NUMERIC(5, 2)	NULL	DEFAULT 0.00	COMMENT '수수료율(%) - 직접예약은 0, OTA는 보통 15~20%',
	`활성여부`	BOOLEAN	NOT NULL	DEFAULT TRUE	COMMENT '현재 사용중인 채널인지 여부',
	`생성시간`	TIMESTAMP	NOT NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '채널 등록 시간'
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

CREATE TABLE `membership_grades` (
	`id`	BIGSERIAL	NOT NULL,
	`code`	VARCHAR(20)	NOT NULL,
	`name`	VARCHAR(50)	NOT NULL,
	`name_en`	VARCHAR(50)	NULL,
	`icon_url`	VARCHAR(500)	NULL,
	`discount_rate`	DECIMAL(5,4)	NOT NULL	DEFAULT 0,
	`point_earn_rate`	DECIMAL(5,4)	NOT NULL	DEFAULT 0.01,
	`min_total_spend`	DECIMAL(12,2)	NULL	DEFAULT 0,
	`min_completed_stays`	INT	NULL	DEFAULT 0,
	`display_order`	INT	NOT NULL	DEFAULT 0,
	`is_active`	BOOLEAN	NOT NULL	DEFAULT true,
	`created_at`	TIMESTAMPTZ	NOT NULL	DEFAULT now(),
	`updated_at`	TIMESTAMPTZ	NOT NULL	DEFAULT now()
);

CREATE TABLE `hotel_info` (
	`id`	VARCHAR(255)	NOT NULL,
	`hotel_id`	BIGSERIAL	NOT NULL,
	`wifi`	BOOLEAN	NULL,
	`swimpool`	BOOLEAN	NULL,
	`health`	BOOLEAN	NULL,
	`spa`	BOOLEAN	NULL,
	`restaurant`	BOOLEAN	NULL,
	`balletparking`	BOOLEAN	NULL,
	`bar`	BOOLEAN	NULL
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

CREATE TABLE `room_blocks` (
	`room_block_id`	BIGINT	NOT NULL,
	`room_id`	BIGINT	NOT NULL,
	`block_start_date`	DATE	NOT NULL,
	`block_end_date`	DATE	NOT NULL,
	`reason`	VARCHAR(255)	NULL,
	`created_at`	TIMESTAMP	NOT NULL	DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `transportaion` (
	`trans_id`	BIGINT	NOT NULL,
	`hotel_id`	BIGINT	NOT NULL,
	`transname`	VARCHAR(200)	NULL,
	`transtime`	INT	NULL,
	`transdepart`	VARCHAR(100)	NULL
);

CREATE TABLE `point_reservation_earnings` (
	`id`	BIGSERIAL	NOT NULL,
	`reservation_id`	BIGINT	NOT NULL,
	`user_id`	BIGINT	NOT NULL,
	`payment_amount`	DECIMAL(12,2)	NOT NULL,
	`earn_rate`	DECIMAL(5,4)	NOT NULL,
	`earned_points`	INT	NOT NULL,
	`point_transaction_id`	BIGINT	NULL,
	`status`	VARCHAR(20)	NOT NULL	DEFAULT 'COMPLETED',
	`earned_at`	TIMESTAMPTZ	NOT NULL	DEFAULT now()
);

CREATE TABLE `wishlists` (
	`user_id`	BIGINT	NOT NULL,
	`hotel_id`	BIGINT	NOT NULL,
	`created_at`	TIMESTAMP	NOT NULL	DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `amenties_type` (
	`편의시설유형id`	BIGINT	NOT NULL,
	`이름`	VARCHAR(200)	NULL
);

CREATE TABLE `season_rates` (
	`시즌요금`	BIGINT	NOT NULL	COMMENT '시즌요금ID - PK',
	`시즌명`	VARCHAR(50)	NOT NULL	COMMENT '시즌명 (예: 성수기, 준성수기, 평시, 비수기)',
	`시즌 시작일`	DATE	NOT NULL	COMMENT '시즌 시작일',
	`시즌 종료일`	DATE	NOT NULL	COMMENT '시즌 종료일',
	`기준요금`	NUMERIC(12, 2)	NOT NULL	COMMENT '기준요금 (평일 기준)',
	`주말요금`	NUMERIC(12, 2)	NOT NULL	COMMENT '주말요금 (금/토/일 기준)',
	`상태`	VARCHAR(20)	NOT NULL	COMMENT '상태 - UPCOMING(예정)/ONGOING(진행중)/ENDED(종료)',
	`등록일시`	TIMESTAMP	NOT NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '등록일시',
	`policy_id`	BIGINT	NOT NULL	COMMENT '정책ID - PK',
	`room_type`	INT	NULL
);

CREATE TABLE `rooms` (
	`room_id`	BIGINT	NOT NULL,
	`room_number`	VARCHAR(50)	NOT NULL,
	`floor`	INT	NULL,
	`status`	INT	NOT NULL	COMMENT '예약가능/예약불가',
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
	`room_type`	INT	NULL	COMMENT '스위트, 디럭스, 스탠다드 등'
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

CREATE TABLE `reservation_grade_discounts` (
	`id`	BIGSERIAL	NOT NULL,
	`reservation_id`	BIGINT	NOT NULL,
	`user_id`	BIGINT	NOT NULL,
	`membership_grade_id`	BIGINT	NOT NULL,
	`discount_rate`	DECIMAL(5,4)	NOT NULL,
	`discount_amount`	DECIMAL(12,2)	NOT NULL,
	`created_at`	TIMESTAMPTZ	NOT NULL	DEFAULT now()
);

CREATE TABLE `bookings` (
	`booking_id`	BIGINT	NOT NULL,
	`user_id`	BIGINT	NULL,
	`hotel_id`	BIGINT	NOT NULL,
	`room_id`	BIGINT	NOT NULL,
	`channel_id`	BIGINT	NOT NULL	COMMENT '채널ID - PK',
	`booking_no`	VARCHAR(50)	NOT NULL,
	`guest_last_name`	VARCHAR(100)	NOT NULL,
	`guest_first_name`	VARCHAR(100)	NOT NULL,
	`guest_phone`	VARCHAR(30)	NOT NULL,
	`guest_email`	VARCHAR(255)	NOT NULL,
	`nationality`	VARCHAR(30)	NOT NULL	DEFAULT 'PENDING',
	`special_request`	VARCHAR(255)	NULL,
	`nights`	INT	NULL,
	`guest_count`	INT	NOT NULL,
	`created_at`	TIMESTAMP	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	`cancelled_at`	TIMESTAMP	NULL,
	`point`	INT	NULL,
	`check_in_date`	DATE	NOT NULL,
	`check_in_time`	TIME	NOT NULL,
	`check_out_date`	DATE	NOT NULL,
	`check_out_time`	TIME	NOT NULL
);

CREATE TABLE `room_rates` (
	`room_rate_id`	BIGINT	NOT NULL,
	`stay_date`	DATE	NOT NULL,
	`price`	NUMERIC(12, 2)	NOT NULL,
	`available_count`	INT	NULL,
	`created_at`	TIMESTAMP	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	`updated_at`	TIMESTAMP	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	`season_rate_id`	BIGINT	NOT NULL	COMMENT '시즌요금ID - PK',
	`season_rate_id2`	BIGINT	NOT NULL	COMMENT '시즌요금ID - PK'
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
	`room_id`	BIGINT	NOT NULL,
	`Field`	VARCHAR(255)	NULL
);

CREATE TABLE `promotiontype` (
	`promotion_type_id`	BIGINT	NOT NULL,
	`promotion_id`	BIGINT	NOT NULL,
	`promotion_context`	TEXT	NULL
);

CREATE TABLE `key_attraction` (
	`attraction_id`	BIGINT	NOT NULL,
	`hotel_id`	BIGINT	NOT NULL,
	`attraction_con`	TEXT	NULL
);

CREATE TABLE `hotel_images` (
	`hotel_image_id`	BIGINT	NOT NULL,
	`hotel_id`	BIGINT	NOT NULL,
	`image_url`	TEXT	NOT NULL,
	`sort_order`	INT	NOT NULL	DEFAULT 0,
	`is_thumbnail`	BOOLEAN	NOT NULL	DEFAULT FALSE
);

CREATE TABLE `rating_categories` (
	`카테고리id`	BIGINT	NULL,
	`평점이름`	VARCHAR(100)	NULL
);

CREATE TABLE `hotels` (
	`hotel_id`	BIGSERIAL	NOT NULL,
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
	`latitude`	VARCHAR(30)	NULL,
	`longitude`	VARCHAR(30)	NULL,
	`type`	INT	NULL
);

CREATE TABLE `promotion` (
	`promotion_id`	BIGINT	NOT NULL,
	`room_id`	BIGINT	NOT NULL,
	`name`	VARCHAR(100)	NOT NULL	COMMENT '프로모션명',
	`desc`	TEXT	NULL	COMMENT '프로모션 설명',
	`discount_type`	ENUM('RATE', 'AMOUNT')	NOT NULL	COMMENT '할인 유형(RATE: %, AMOUNT: 금액)',
	`discount_value`	DECIMAL(10, 2)	NOT NULL	COMMENT '할인값',
	`start_date`	DATETIME	NOT NULL	COMMENT '시작일',
	`end_date`	DATETIME	NOT NULL	COMMENT '종료일',
	`res_count`	INT	NULL,
	`conversion_rate`	DECIMAL(5,2)	NULL,
	`status`	VARCHAR(20)	NULL
);

CREATE TABLE `point_reservation_usages` (
	`id`	BIGSERIAL	NOT NULL,
	`reservation_id`	BIGINT	NOT NULL,
	`payment_id`	BIGINT	NOT NULL,
	`user_id`	BIGINT	NOT NULL,
	`used_points`	INT	NOT NULL,
	`point_transaction_id`	BIGINT	NULL,
	`created_at`	TIMESTAMPTZ	NOT NULL	DEFAULT now()
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

CREATE TABLE `coupon` (
	`coupon_id`	BIGINT	NOT NULL,
	`coupon_code`	VARCHAR(50)	NOT NULL,
	`coupon_name`	VARCHAR(255)	NOT NULL,
	`discount_type`	ENUM('FIXED', 'RATE')	NOT NULL,
	`discount_value`	BIGINT	NOT NULL,
	`min_order_amount`	BIGINT	NOT NULL,
	`max_discount_amount`	BIGINT	NOT NULL,
	`created_at`	DATETIME	NOT NULL,
	`expiration_date`	DATETIME	NOT NULL,
	`coupon_status`	ENUM('ACTIVE', 'EXPIRED')	NOT NULL
);

CREATE TABLE `point_transactions` (
	`id`	BIGSERIAL	NOT NULL,
	`user_id`	BIGINT	NOT NULL,
	`transaction_type`	VARCHAR(20)	NOT NULL,
	`amount`	INT	NOT NULL,
	`balance_after`	INT	NOT NULL,
	`reservation_id`	BIGINT	NULL,
	`payment_id`	BIGINT	NULL,
	`earn_rate`	DECIMAL(5,4)	NULL,
	`description`	VARCHAR(255)	NULL,
	`expires_at`	TIMESTAMPTZ	NULL,
	`created_at`	TIMESTAMPTZ	NOT NULL	DEFAULT now()
);

CREATE TABLE `hotel_amenties` (
	`id`	BIGINT	NULL,
	`호텔아이디`	BIGINT	NOT NULL,
	`편의시설유형id`	BIGINT	NOT NULL,
	`유형`	TEXT	NULL,
	`이름`	VARCHAR(200)	NULL
);

CREATE TABLE `users` (
	`user_id`	BIGINT	NOT NULL,
	`email`	VARCHAR(255)	NOT NULL,
	`password`	VARCHAR(255)	NOT NULL,
	`name`	VARCHAR(100)	NOT NULL,
	`phone`	VARCHAR(30)	NULL,
	`role`	ENUM('CUSTOMER', 'HOTEL_MANAGER', 'ADMIN', 'SUPER_ADMIN')	NOT NULL	DEFAULT 'CUSTOMER',
	`status`	ENUM('ACTIVE', 'DORMANT', 'LOCKED', 'WITHDRAWN')	NOT NULL	DEFAULT TRUE,
	`created_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	`updated_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	`social_login`	VARCHAR(10)	NULL,
	`social_login_id`	VARCHAR(30)	NULL,
	`Membership`	Enum('bronze','silver','gold','platinum')	NULL
);

CREATE TABLE `reviews_photo` (
	`id`	BIGINT	NOT NULL,
	`리뷰_id`	BIGINT	NOT NULL,
	`사진경로`	VARCAHR(500)	NOT NULL,
	`사진순서`	BIGINT	NULL
);

CREATE TABLE `weekday_rates` (
	`요일별요금ID`	BIGINT	NOT NULL,
	`요일`	ENUM	NULL,
	`요금변동률`	DECIMAL(5,2)	NULL,
	`시즌요금`	BIGINT	NOT NULL	COMMENT '시즌요금ID - PK'
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

ALTER TABLE `promotionsale` ADD CONSTRAINT `PK_PROMOTIONSALE` PRIMARY KEY (
	`promotion_sale_id`
);

ALTER TABLE `point_transaction_types` ADD CONSTRAINT `PK_POINT_TRANSACTION_TYPES` PRIMARY KEY (
	`code`
);

ALTER TABLE `trip_type_` ADD CONSTRAINT `PK_TRIP_TYPE_` PRIMARY KEY (
	`여행유형아이디`
);

ALTER TABLE `room_images` ADD CONSTRAINT `PK_ROOM_IMAGES` PRIMARY KEY (
	`room_image_id`
);

ALTER TABLE `membership_grade_histories` ADD CONSTRAINT `PK_MEMBERSHIP_GRADE_HISTORIES` PRIMARY KEY (
	`id`
);

ALTER TABLE `channels` ADD CONSTRAINT `PK_CHANNELS` PRIMARY KEY (
	`채널ID`
);

ALTER TABLE `checkin_qr_codes` ADD CONSTRAINT `PK_CHECKIN_QR_CODES` PRIMARY KEY (
	`QR_ID`
);

ALTER TABLE `qr_code_statuses` ADD CONSTRAINT `PK_QR_CODE_STATUSES` PRIMARY KEY (
	`code`
);

ALTER TABLE `membership_grades` ADD CONSTRAINT `PK_MEMBERSHIP_GRADES` PRIMARY KEY (
	`id`
);

ALTER TABLE `hotel_info` ADD CONSTRAINT `PK_HOTEL_INFO` PRIMARY KEY (
	`id`
);

ALTER TABLE `refunds` ADD CONSTRAINT `PK_REFUNDS` PRIMARY KEY (
	`refund_id`
);

ALTER TABLE `room_blocks` ADD CONSTRAINT `PK_ROOM_BLOCKS` PRIMARY KEY (
	`room_block_id`
);

ALTER TABLE `transportaion` ADD CONSTRAINT `PK_TRANSPORTAION` PRIMARY KEY (
	`trans_id`
);

ALTER TABLE `point_reservation_earnings` ADD CONSTRAINT `PK_POINT_RESERVATION_EARNINGS` PRIMARY KEY (
	`id`
);

ALTER TABLE `wishlists` ADD CONSTRAINT `PK_WISHLISTS` PRIMARY KEY (
	`user_id`,
	`hotel_id`
);

ALTER TABLE `amenties_type` ADD CONSTRAINT `PK_AMENTIES_TYPE` PRIMARY KEY (
	`편의시설유형id`
);

ALTER TABLE `season_rates` ADD CONSTRAINT `PK_SEASON_RATES` PRIMARY KEY (
	`시즌요금`
);

ALTER TABLE `rooms` ADD CONSTRAINT `PK_ROOMS` PRIMARY KEY (
	`room_id`
);

ALTER TABLE `qr_code_scan_logs` ADD CONSTRAINT `PK_QR_CODE_SCAN_LOGS` PRIMARY KEY (
	`QRLOG_ID`
);

ALTER TABLE `reservation_grade_discounts` ADD CONSTRAINT `PK_RESERVATION_GRADE_DISCOUNTS` PRIMARY KEY (
	`id`
);

ALTER TABLE `bookings` ADD CONSTRAINT `PK_BOOKINGS` PRIMARY KEY (
	`booking_id`
);

ALTER TABLE `room_rates` ADD CONSTRAINT `PK_ROOM_RATES` PRIMARY KEY (
	`room_rate_id`
);

ALTER TABLE `rate_policies` ADD CONSTRAINT `PK_RATE_POLICIES` PRIMARY KEY (
	`policy_id`
);

ALTER TABLE `promotiontype` ADD CONSTRAINT `PK_PROMOTIONTYPE` PRIMARY KEY (
	`promotion_type_id`
);

ALTER TABLE `key_attraction` ADD CONSTRAINT `PK_KEY_ATTRACTION` PRIMARY KEY (
	`attraction_id`
);

ALTER TABLE `hotel_images` ADD CONSTRAINT `PK_HOTEL_IMAGES` PRIMARY KEY (
	`hotel_image_id`
);

ALTER TABLE `rating_categories` ADD CONSTRAINT `PK_RATING_CATEGORIES` PRIMARY KEY (
	`카테고리id`
);

ALTER TABLE `hotels` ADD CONSTRAINT `PK_HOTELS` PRIMARY KEY (
	`hotel_id`
);

ALTER TABLE `promotion` ADD CONSTRAINT `PK_PROMOTION` PRIMARY KEY (
	`promotion_id`
);

ALTER TABLE `point_reservation_usages` ADD CONSTRAINT `PK_POINT_RESERVATION_USAGES` PRIMARY KEY (
	`id`
);

ALTER TABLE `tag_type` ADD CONSTRAINT `PK_TAG_TYPE` PRIMARY KEY (
	`태그_id`
);

ALTER TABLE `reivews_rating` ADD CONSTRAINT `PK_REIVEWS_RATING` PRIMARY KEY (
	`id`
);

ALTER TABLE `coupon` ADD CONSTRAINT `PK_COUPON` PRIMARY KEY (
	`coupon_id`
);

ALTER TABLE `point_transactions` ADD CONSTRAINT `PK_POINT_TRANSACTIONS` PRIMARY KEY (
	`id`
);

ALTER TABLE `hotel_amenties` ADD CONSTRAINT `PK_HOTEL_AMENTIES` PRIMARY KEY (
	`id`
);

ALTER TABLE `users` ADD CONSTRAINT `PK_USERS` PRIMARY KEY (
	`user_id`
);

ALTER TABLE `reviews_photo` ADD CONSTRAINT `PK_REVIEWS_PHOTO` PRIMARY KEY (
	`id`
);

ALTER TABLE `weekday_rates` ADD CONSTRAINT `PK_WEEKDAY_RATES` PRIMARY KEY (
	`요일별요금ID`
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

