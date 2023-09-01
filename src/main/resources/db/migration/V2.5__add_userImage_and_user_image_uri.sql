create table user_image
(
    id         bigint not null auto_increment,
    user_id    bigint,
    image_uri  varchar(255),
    created_at datetime(6) not null,
    is_deleted    bit     not null default 1,

    primary key (id)
);

alter table user_image
    add constraint fk_user_image_user_user_id
        foreign key (user_id)
            references `user` (id);

INSERT INTO user_image (user_id, image_uri, created_at) SELECT id, image_uri, created_at FROM user;

alter table user drop `image_uri`