create table address
(
    id         bigint auto_increment
        primary key,
    user_id    bigint       not null,
    receiver   varchar(255) null,
    phone      varchar(255) null,
    province   varchar(255) null,
    city       varchar(255) null,
    area       varchar(255) null,
    detail     varchar(255) not null,
    is_default int          null
)
    charset = utf8mb4;

create index idx_address_user_id
    on address (user_id);

create table banner
(
    id          bigint auto_increment
        primary key,
    image_url   varchar(255)  not null,
    link_type   varchar(255)  null,
    link_target varchar(255)  null,
    sort        int default 0 null,
    status      int           null
)
    charset = utf8mb4;

create index idx_banner_status
    on banner (status);

create table cart_item
(
    id         bigint auto_increment
        primary key,
    user_id    bigint                             not null,
    product_id bigint                             not null,
    quantity   int      default 1                 not null,
    checked    int                                null,
    created_at datetime default CURRENT_TIMESTAMP null,
    constraint uk_cart_user_product
        unique (user_id, product_id)
)
    charset = utf8mb4;

create index idx_cart_user_id
    on cart_item (user_id);

create table category
(
    id        bigint auto_increment
        primary key,
    name      varchar(255)     null,
    parent_id bigint default 0 null,
    sort      int    default 0 null,
    status    int              null
)
    charset = utf8mb4;

create index idx_category_parent_id
    on category (parent_id);

create table menu
(
    id        bigint auto_increment
        primary key,
    parent_id bigint default 0 null,
    name      varchar(255)     null,
    path      varchar(255)     null,
    component varchar(255)     null,
    type      varchar(255)     null,
    perm_code varchar(255)     null,
    sort      int    default 0 null,
    visible   int              null
)
    charset = utf8mb4;

create index idx_menu_parent_id
    on menu (parent_id);

create table operation_log
(
    id             bigint auto_increment
        primary key,
    admin_id       bigint       null,
    admin_username varchar(64)  null,
    action         varchar(64)  not null,
    target         varchar(128) null,
    detail         varchar(512) null,
    ip             varchar(64)  null,
    created_at     datetime     null
);

create index idx_action
    on operation_log (action);

create index idx_admin_id
    on operation_log (admin_id);

create index idx_created_at
    on operation_log (created_at);

create table `order`
(
    id               bigint auto_increment
        primary key,
    order_no         varchar(255)                       null,
    user_id          bigint                             not null,
    total_amount     decimal(38, 2)                     null,
    pay_amount       decimal(38, 2)                     null,
    status           int                                null,
    address_snapshot varchar(255)                       null,
    created_at       datetime default CURRENT_TIMESTAMP null,
    paid_at          datetime                           null,
    shipped_at       datetime                           null,
    finished_at      datetime                           null,
    constraint uk_order_no
        unique (order_no)
)
    charset = utf8mb4;

create index idx_order_status
    on `order` (status);

create index idx_order_user_id
    on `order` (user_id);

create table order_delivery
(
    order_id        bigint      not null
        primary key,
    express_no      varchar(64) null,
    express_company varchar(64) null
)
    charset = utf8mb4;

create table order_item
(
    id                    bigint auto_increment
        primary key,
    order_id              bigint         not null,
    product_id            bigint         not null,
    product_name_snapshot varchar(255)   null,
    price_snapshot        decimal(38, 2) null,
    quantity              int            not null,
    image_snapshot        varchar(255)   null
)
    charset = utf8mb4;

create index idx_order_item_order_id
    on order_item (order_id);

create table product
(
    id          bigint auto_increment
        primary key,
    category_id bigint                             null,
    name        varchar(255)                       null,
    brief       varchar(255)                       null,
    price       decimal(38, 2)                     null,
    stock       int      default 0                 not null,
    status      varchar(255)                       null,
    cover_url   varchar(255)                       null,
    detail_html varchar(255)                       null,
    created_at  datetime default CURRENT_TIMESTAMP null
)
    charset = utf8mb4;

create index idx_product_category_id
    on product (category_id);

create index idx_product_name
    on product (name);

create index idx_product_status
    on product (status);

create table product_image
(
    id         bigint auto_increment
        primary key,
    product_id bigint        not null,
    url        varchar(255)  not null,
    sort       int default 0 null
)
    charset = utf8mb4;

create index idx_product_image_product_id
    on product_image (product_id);

create table role
(
    id        bigint auto_increment
        primary key,
    role_key  varchar(255) null,
    role_name varchar(255) null,
    constraint uk_role_key
        unique (role_key)
)
    charset = utf8mb4;

create table role_menu
(
    id      bigint auto_increment
        primary key,
    role_id bigint not null,
    menu_id bigint not null,
    constraint uk_role_menu
        unique (role_id, menu_id)
)
    charset = utf8mb4;

create index idx_role_menu_menu_id
    on role_menu (menu_id);

create index idx_role_menu_role_id
    on role_menu (role_id);

create table user
(
    id            bigint auto_increment
        primary key,
    username      varchar(255)                       null,
    phone         varchar(255)                       null,
    password_hash varchar(255)                       not null,
    status        int                                null,
    role_id       bigint                             null,
    created_at    datetime default CURRENT_TIMESTAMP null,
    constraint uk_user_phone
        unique (phone),
    constraint uk_user_username
        unique (username)
)
    charset = utf8mb4;

create index idx_user_role_id
    on user (role_id);

create table user_favorite
(
    id         bigint auto_increment
        primary key,
    user_id    bigint      not null,
    product_id bigint      not null,
    created_at datetime    null,
    constraint uk_user_favorite
        unique (user_id, product_id)
)
    charset = utf8mb4;

create index idx_user_favorite_user_id
    on user_favorite (user_id);

create index idx_user_favorite_product_id
    on user_favorite (product_id);

create table user_footprint
(
    id         bigint auto_increment
        primary key,
    user_id    bigint      not null,
    product_id bigint      not null,
    viewed_at  datetime    null,
    constraint uk_user_footprint
        unique (user_id, product_id)
)
    charset = utf8mb4;

create index idx_user_footprint_user_id
    on user_footprint (user_id);

create index idx_user_footprint_viewed_at
    on user_footprint (viewed_at);

