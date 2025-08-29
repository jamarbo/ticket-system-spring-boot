
CREATE TYPE ticket_priority AS ENUM ('LOW','MED','HIGH');
CREATE TYPE ticket_status   AS ENUM ('OPEN','IN_PROGRESS','CLOSED');

CREATE TABLE users (
  id UUID PRIMARY KEY,
  email VARCHAR(120) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  name VARCHAR(120) NOT NULL,
  role VARCHAR(10) NOT NULL
);

CREATE TABLE tickets (
  id UUID PRIMARY KEY,
  title VARCHAR(120) NOT NULL,
  description TEXT,
  priority ticket_priority NOT NULL,
  status ticket_status NOT NULL,
  assigned_to_id UUID REFERENCES users(id),
  tags JSONB,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL,
  created_by UUID NOT NULL REFERENCES users(id)
);

CREATE INDEX idx_tickets_status     ON tickets(status);
CREATE INDEX idx_tickets_priority   ON tickets(priority);
CREATE INDEX idx_tickets_created_at ON tickets(created_at DESC);
