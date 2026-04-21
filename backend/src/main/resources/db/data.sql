-- This file stocks our shelves with fake data so the Frontend developer can test!

-- Add 1 Fake Consumer
INSERT INTO consumer (name, email, phone, password, org) 
VALUES ('John Doe', 'john@example.com', '555-0199', 'fake_hashed_pass', 'Johns Bakery') 
ON CONFLICT (email) DO NOTHING;

-- Add 1 Fake Supplier
INSERT INTO supplier (name, email, phone, org, rating) 
VALUES ('Farm Fresh Inc', 'farm@example.com', '555-0299', 'Farm Fresh Co', 4.8)
ON CONFLICT (email) DO NOTHING;

-- Add 1 Fake Order belonging to John Doe
INSERT INTO "order" (consumer_id, order_date, total_amount) 
VALUES (1, CURRENT_DATE, 150.50);
