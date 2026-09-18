create table customers(
    id uuid primary key ,
    business_id uuid not null ,
    full_name varchar(160) not null ,
    phone_number varchar(16) not null ,
    created_at timestamptz not null ,

    constraint fk_customers_business
                      foreign key (business_id)
                      references businesses (id)
                      on delete restrict ,


    constraint ck_customer_full_name_not_blank
                      check ( char_length(btrim(full_name)) between 1 AND 160),

    CONSTRAINT ck_customers_phone_number_e164
        CHECK (
            phone_number ~ '^\+[1-9][0-9]{7,14}$'
            )

);

create index idx_customer_business_id
on customers (business_id);