
DO $$
DECLARE
    admin_id UUID := gen_random_uuid();
    user_id UUID := gen_random_uuid();
BEGIN
    -- Seed users with a valid password.
    -- The password for both users is 'password'
    INSERT INTO users (id, email, password_hash, name, role) VALUES
      (admin_id, 'admin@demo.com', '$2a$10$Xi2/fghV7ccIiNI4RlgKgupdNRo/OCC28YfRGpXNVr0QG0/R3jovW', 'Admin User', 'ADMIN'),
      (user_id, 'user@demo.com', '$2a$10$Xi2/fghV7ccIiNI4RlgKgupdNRo/OCC28YfRGpXNVr0QG0/R3jovW', 'Regular User', 'USER');

    -- Seed tickets
    INSERT INTO tickets (id, title, description, status, priority, created_at, updated_at, assigned_to_id, created_by) VALUES
      (gen_random_uuid(), 'Fix login button', 'The login button on the main page is not working.', 'OPEN', 'HIGH', NOW(), NOW(), admin_id, admin_id),
      (gen_random_uuid(), 'Update user profile page', 'The user profile page needs a new design.', 'IN_PROGRESS', 'MED', NOW(), NOW(), user_id, admin_id),
      (gen_random_uuid(), 'Database migration failed', 'The V3 migration script is failing on production.', 'OPEN', 'HIGH', NOW(), NOW(), admin_id, user_id),
      (gen_random_uuid(), 'Add pagination to user list', 'The user list should be paginated to improve performance.', 'CLOSED', 'LOW', NOW(), NOW(), user_id, user_id),
      (gen_random_uuid(), 'Translate application to Spanish', 'The entire application needs to be translated to Spanish.', 'OPEN', 'MED', NOW(), NOW(), NULL, admin_id);
END $$;
