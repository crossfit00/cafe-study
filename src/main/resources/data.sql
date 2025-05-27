CREATE TABLE member (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) NOT NULL,
    gender VARCHAR(255) NOT NULL,
    birth DATE NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    modified_at DATETIME NOT NULL,

    UNIQUE KEY uq_member_email (email),
    UNIQUE KEY uq_member_phone (phone_number)
);

CREATE TABLE member_history (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    modified_at DATETIME NOT NULL,

    KEY idx_member_id_status (member_id, status)
);

CREATE TABLE item (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(64, 0) NOT NULL,
    stock BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    modified_at DATETIME NOT NULL
);

CREATE TABLE orders (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    total_price DECIMAL(64, 0) NOT NULL,
    status VARCHAR(255) NOT NULL,
    member_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    modified_at DATETIME NOT NULL
);

CREATE TABLE orders_history (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    modified_at DATETIME NOT NULL,

    KEY idx_order_id_status (order_id, status)
);

CREATE TABLE orders_item (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    quantity INT NOT NULL,
    unit_price DECIMAL(64, 0) NOT NULL,
    item_id BIGINT NOT NULL,
    order_id BIGINT,
    created_at DATETIME NOT NULL,
    modified_at DATETIME NOT NULL
);

CREATE TABLE payment (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(64, 0) NOT NULL,
    order_id BIGINT NOT NULL,
    payment_uuid VARCHAR(255) NOT NULL,  -- 결제 PG사에서 응답 준 고유 아이디
    status VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    modified_at DATETIME NOT NULL
);

CREATE TABLE payment_history (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    modified_at DATETIME NOT NULL,

    KEY idx_pay_id_status (payment_id, status)
);

INSERT INTO member (name, email, phone_number, gender, birth, status, created_at, modified_at) VALUES ('규니', 'wjdrbs966@naver.com', '010-4024-7706', 'MALE', '1996-12-06', 'SERVICE', now(), now());
INSERT INTO item (name, price, stock, created_at, modified_at) VALUES ('펩시 제로 콜라', 1500, 10, now(), now());
INSERT INTO item (name, price, stock, created_at, modified_at) VALUES ('토비의 스프링 3.0', 25000, 15, now(), now());