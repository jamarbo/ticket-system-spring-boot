-- Add demo user for testing
INSERT INTO users (id, email, password_hash, name, role)
VALUES (
    gen_random_uuid(),
    'demo@example.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMye7xdg68lJRJVNsMMjrPuRUO5tOKj.8Se', -- 'password'
    'Demo User',
    'USER'
) ON CONFLICT (email) DO NOTHING;