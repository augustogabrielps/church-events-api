-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =========================
-- USERS
-- =========================
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       name TEXT NOT NULL,
                       email TEXT NOT NULL UNIQUE,
                       role TEXT NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- =========================
-- EVENTS
-- =========================
CREATE TABLE events (
                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                        title TEXT NOT NULL,
                        description TEXT,
                        location TEXT,
                        status TEXT NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- =========================
-- EVENT DATES
-- =========================
CREATE TABLE event_dates (
                             id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                             event_id UUID NOT NULL,
                             event_date DATE NOT NULL,
                             start_time TIME,
                             end_time TIME,

                             CONSTRAINT fk_event_dates_event
                                 FOREIGN KEY (event_id)
                                     REFERENCES events(id)
                                     ON DELETE CASCADE
);

-- =========================
-- VOLUNTEER ROLES
-- =========================
CREATE TABLE volunteer_roles (
                                 id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                 event_id UUID NOT NULL,
                                 name TEXT NOT NULL,
                                 description TEXT,
                                 required_people INT NOT NULL,

                                 CONSTRAINT fk_roles_event
                                     FOREIGN KEY (event_id)
                                         REFERENCES events(id)
                                         ON DELETE CASCADE
);

-- =========================
-- VOLUNTEER SIGNUPS
-- =========================
CREATE TABLE volunteer_signups (
                                   id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                   user_id UUID NOT NULL,
                                   role_id UUID NOT NULL,
                                   event_date_id UUID NOT NULL,
                                   status TEXT NOT NULL,
                                   created_at TIMESTAMP NOT NULL DEFAULT NOW(),

                                   CONSTRAINT fk_signup_user
                                       FOREIGN KEY (user_id)
                                           REFERENCES users(id)
                                           ON DELETE CASCADE,

                                   CONSTRAINT fk_signup_role
                                       FOREIGN KEY (role_id)
                                           REFERENCES volunteer_roles(id)
                                           ON DELETE CASCADE,

                                   CONSTRAINT fk_signup_event_date
                                       FOREIGN KEY (event_date_id)
                                           REFERENCES event_dates(id)
                                           ON DELETE CASCADE
);