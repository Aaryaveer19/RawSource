-- This file builds our empty shelves!

CREATE TABLE IF NOT EXISTS consumer (
    consumer_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50),
    password VARCHAR(255),
    org VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS "order" (
    order_id SERIAL PRIMARY KEY,
    consumer_id BIGINT NOT NULL,
    order_date DATE NOT NULL,
    total_amount NUMERIC(15, 2),
    CONSTRAINT fk_consumer FOREIGN KEY (consumer_id) REFERENCES consumer(consumer_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS supplier (
    supplier_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(50),
    org VARCHAR(255),
    rating NUMERIC(3,2)
);
