create table restaurant_db.public.orders
(
    order_id bigint not null constraint order_pk primary key,
    order_address     varchar   not null,
    order_phone       varchar   not null,
    order_status      varchar   not null,
    order_create_date timestamp not null,
    order_update_date timestamp,
    order_dishes      varchar   not null
);
create unique index order_order_id_uindex on orders (order_id);

create table restaurant_db.public.delivers
(
    deliver_id bigint,
    deliver_busy boolean,
    deliver_contact varchar(255),
    deliver_create_date timestamp,
    deliver_name varchar(255),
    deliver_update_date timestamp
);
create table restaurant_db.public.dishes
(
    dish_id bigint,
    dish_available boolean,
    dish_create_date timestamp,
    dish_name varchar(255),
    dish_recipe varchar(255),
    dish_update_date timestamp
);
