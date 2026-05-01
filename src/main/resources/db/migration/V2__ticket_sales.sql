-- =========================
-- TICKET TYPES
-- =========================
CREATE TABLE ticket_types (
                              id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              event_id UUID NOT NULL,
                              name TEXT NOT NULL,
                              description TEXT,
                              price NUMERIC(10,2) NOT NULL,
                              available_quantity INTEGER NOT NULL,
                              created_at TIMESTAMP NOT NULL DEFAULT NOW(),

                              CONSTRAINT fk_ticket_types_event
                                  FOREIGN KEY (event_id)
                                      REFERENCES events(id)
                                      ON DELETE CASCADE,

                              CONSTRAINT chk_ticket_types_price
                                  CHECK (price >= 0),

                              CONSTRAINT chk_ticket_types_available_quantity
                                  CHECK (available_quantity >= 0)
);

-- =========================
-- TICKET SALES
-- =========================
CREATE TABLE ticket_sales (
                              id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              event_id UUID NOT NULL,
                              ticket_type_id UUID NOT NULL,
                              seller_id UUID NOT NULL,
                              buyer_name TEXT NOT NULL,
                              quantity INTEGER NOT NULL,
                              total_amount NUMERIC(10,2) NOT NULL,
                              status TEXT NOT NULL,
                              created_at TIMESTAMP NOT NULL DEFAULT NOW(),

                              CONSTRAINT fk_ticket_sales_event
                                  FOREIGN KEY (event_id)
                                      REFERENCES events(id)
                                      ON DELETE CASCADE,

                              CONSTRAINT fk_ticket_sales_ticket_type
                                  FOREIGN KEY (ticket_type_id)
                                      REFERENCES ticket_types(id)
                                      ON DELETE CASCADE,

                              CONSTRAINT fk_ticket_sales_seller
                                  FOREIGN KEY (seller_id)
                                      REFERENCES users(id)
                                      ON DELETE CASCADE,

                              CONSTRAINT chk_ticket_sales_quantity
                                  CHECK (quantity > 0),

                              CONSTRAINT chk_ticket_sales_total_amount
                                  CHECK (total_amount >= 0)
);

-- =========================
-- PAYMENTS
-- =========================
CREATE TABLE payments (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          sale_id UUID NOT NULL,
                          amount NUMERIC(10,2) NOT NULL,
                          method TEXT NOT NULL,
                          status TEXT NOT NULL,
                          paid_at TIMESTAMP,
                          created_at TIMESTAMP NOT NULL DEFAULT NOW(),

                          CONSTRAINT fk_payments_sale
                              FOREIGN KEY (sale_id)
                                  REFERENCES ticket_sales(id)
                                  ON DELETE CASCADE,

                          CONSTRAINT chk_payments_amount
                              CHECK (amount >= 0)
);
