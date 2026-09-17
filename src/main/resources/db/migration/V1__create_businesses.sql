CREATE TABLE businesses (
                            id UUID PRIMARY KEY,

                            name VARCHAR(160) NOT NULL,

                            created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT ck_businesses_name_not_blank
                                CHECK (length(btrim(name)) > 0)
);