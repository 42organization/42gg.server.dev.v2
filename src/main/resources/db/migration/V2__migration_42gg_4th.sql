create table coin_history
(
    id         bigint not null auto_increment,
    amount     integer,
    created_at datetime(6) not null,
    history    varchar(30),
    user_id    bigint,
    primary key (id)
);

create table coin_policy
(
    id         bigint not null auto_increment,
    user_id    bigint,
    attendance integer,
    created_at datetime(6) not null,
    normal     integer,
    rank_lose  integer,
    rank_win   integer,
    primary key (id)
);

create table item
(
    id         bigint  not null auto_increment,
    content    varchar(255),
    created_at datetime(6) not null,
    discount   integer,
    image_uri  varchar(255),
    is_visible bit     not null,
    name       varchar(30),
    price      integer not null,
    primary key (id)
);

create table megaphone
(
    id      bigint not null auto_increment,
    content varchar(30),
    used_at date   not null,
    user_id bigint not null,
    primary key (id)
);

alter table ranks
    add column tier_id bigint;

create table receipt
(
    id                 bigint       not null auto_increment,
    owner_intra_id     varchar(255) not null,
    created_at         datetime(6) not null,
    purchaser_intra_id varchar(255) not null,
    status             varchar(255) not null,
    item_id            bigint       not null,
    primary key (id)
);

create table tier
(
    id        bigint not null auto_increment,
    image_uri varchar(255),
    name      varchar(255),
    primary key (id)
);

alter table user
    add column background varchar(255) default "BASIC";

alter table user
    add column edge varchar(255) default "BASIC";

alter table user
    add column gg_coin integer default 0;

alter table user
    add column text_color varchar(10);

alter table coin_history
    add constraint fk_coin_history_user_user_id
        foreign key (user_id)
            references user (id);

alter table coin_policy
    add constraint fk_coin_policy_user_user_id
        foreign key (user_id)
            references user (id);

alter table megaphone
    add constraint fk_megaphone_user_user_id
        foreign key (user_id)
            references user (id);

alter table ranks
    add constraint fk_ranks_tier_tier_id
        foreign key (tier_id)
            references tier (id);

alter table receipt
    add constraint fk_receipt_item_item_id
        foreign key (item_id)
            references item (id);