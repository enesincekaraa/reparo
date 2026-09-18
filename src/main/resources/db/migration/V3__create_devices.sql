ALTER TABLE customers
    ADD CONSTRAINT uq_customers_business_id_id
        UNIQUE (business_id, id);

CREATE TABLE devices
(
    id            UUID         PRIMARY KEY,
    business_id   UUID         NOT NULL,
    customer_id   UUID         NOT NULL,
    device_type   VARCHAR(32)  NOT NULL,
    brand         VARCHAR(80)  NOT NULL,
    model         VARCHAR(120),
    serial_number VARCHAR(100),
    created_at    TIMESTAMPTZ  NOT NULL,

    CONSTRAINT fk_devices_customer_tenant
        FOREIGN KEY (business_id, customer_id)
            REFERENCES customers (business_id, id)
            ON DELETE RESTRICT,

    CONSTRAINT chk_devices_type
        CHECK (device_type IN (
                               'PHONE',
                               'TABLET',
                               'LAPTOP',
                               'DESKTOP',
                               'GAME_CONSOLE',
                               'OTHER'
            )),

    CONSTRAINT chk_devices_brand
        CHECK (
            char_length(brand) BETWEEN 1 AND 80
                AND brand = btrim(brand)
            ),

    CONSTRAINT chk_devices_model
        CHECK (
            model IS NULL
                OR (
                char_length(model) BETWEEN 1 AND 120
                    AND model = btrim(model)
                )
            ),

    CONSTRAINT chk_devices_serial_number
        CHECK (
            serial_number IS NULL
                OR (
                char_length(serial_number) BETWEEN 1 AND 100
                    AND serial_number = btrim(serial_number)
                )
            )
);

CREATE INDEX idx_devices_business_customer
    ON devices (business_id, customer_id);