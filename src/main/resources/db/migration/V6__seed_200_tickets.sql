-- Seed 200 demo tickets (idempotent). V5 was applied empty; this migration performs the actual insert.
-- Inserts only if there are no records tagged with seed = 'v6-demo'.

WITH user_ctx AS (
  SELECT
    COALESCE(
      (SELECT id FROM users WHERE email = 'admin@demo.com' LIMIT 1),
      (SELECT id FROM users WHERE role = 'ADMIN' LIMIT 1),
      (SELECT id FROM users LIMIT 1)
    ) AS admin_id,
    COALESCE(
      (SELECT id FROM users WHERE email = 'user@demo.com' LIMIT 1),
      (SELECT id FROM users WHERE role = 'USER' LIMIT 1),
      (SELECT id FROM users LIMIT 1)
    ) AS user_id
), ins AS (
  INSERT INTO tickets (
    id, title, description, status, priority, tags, created_at, updated_at, assigned_to_id, created_by
  )
  SELECT
    gen_random_uuid(),
    format('Demo ticket #%s', gs.n) AS title,
    format('Seeded demo ticket number %s for testing pagination, filters, and search.', gs.n) AS description,
    CASE (gs.n % 3)
      WHEN 0 THEN 'OPEN'
      WHEN 1 THEN 'IN_PROGRESS'
      ELSE 'CLOSED'
    END::ticket_status AS status,
    CASE (gs.n % 3)
      WHEN 0 THEN 'LOW'
      WHEN 1 THEN 'MED'
      ELSE 'HIGH'
    END::ticket_priority AS priority,
    jsonb_build_object('seed','v6-demo','n',gs.n) AS tags,
    now() - ((gs.n % 60)::text || ' days')::interval AS created_at,
    now() - ((gs.n % 60)::text || ' days')::interval AS updated_at,
    (SELECT CASE WHEN (gs.n % 2) = 0 THEN admin_id ELSE user_id END FROM user_ctx) AS assigned_to_id,
    (SELECT admin_id FROM user_ctx) AS created_by
  FROM generate_series(1, 200) AS gs(n)
  WHERE NOT EXISTS (
    SELECT 1 FROM tickets WHERE tags ->> 'seed' = 'v6-demo'
  )
  RETURNING 1
)
SELECT COALESCE(count(*), 0) AS inserted_count FROM ins;
