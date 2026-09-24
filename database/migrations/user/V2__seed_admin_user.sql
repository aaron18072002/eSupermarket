-- V2__seed_admin_user.sql
-- Native PostgreSQL pgcrypto crypt with Blowfish (BCrypt)

-- 1. Seed Admin User (Password: admin123456)
INSERT INTO users (id, email, password, full_name, phone, address, status)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'admin@esupermarket.com',
    crypt('admin123456', gen_salt('bf', 10)),
    'System Administrator',
    '+84901234567',
    'Central Operations, Tech Park',
    'ACTIVE'
) ON CONFLICT (id) DO NOTHING;

-- Seed Admin Roles
INSERT INTO user_roles (user_id, role)
VALUES 
    ('00000000-0000-0000-0000-000000000001', 'ROLE_ADMIN'),
    ('00000000-0000-0000-0000-000000000001', 'ROLE_CUSTOMER')
ON CONFLICT (user_id, role) DO NOTHING;

-- 2. Seed Sample Customer (Password: customer123456)
INSERT INTO users (id, email, password, full_name, phone, address, status)
VALUES (
    '00000000-0000-0000-0000-000000000002',
    'customer@esupermarket.com',
    crypt('customer123456', gen_salt('bf', 10)),
    'Jane Customer',
    '+84909876543',
    '456 Market Street, District 1',
    'ACTIVE'
) ON CONFLICT (id) DO NOTHING;

-- Seed Customer Roles
INSERT INTO user_roles (user_id, role)
VALUES 
    ('00000000-0000-0000-0000-000000000002', 'ROLE_CUSTOMER')
ON CONFLICT (user_id, role) DO NOTHING;
