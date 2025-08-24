create table users
(
    email      varchar(100)                             not null,
    first_name varchar(20)                              not null,
    last_name  varchar(20)                              not null,
    role                varchar(10) default "EXTERNAL"  not null,
    password            varchar(100)                    null,
    id         BINARY(16) default (UUID_TO_BIN(UUID())) not null primary key,
    constraint role_check check (
        (role <> 'EXTERNAL' AND Password IS NOT NULL) OR
        (role = 'EXTERNAL' AND Password IS NULL)
    )
);

create table tags
(
    id   BIGINT auto_increment primary key,
    name varchar(20) not null
);

create table items
(
    id    BIGINT auto_increment primary key,
    titel varchar(200)  not null,
    place varchar(100)  not null,
    topic varchar(100)  not null,
    note  varchar(500)  null
);

create table item_tags
(
    tag_id  BIGINT not null,
    item_id BIGINT not null,
    constraint item_tags_pk
        primary key (tag_id, item_id),
    constraint item_tags_items_id_fk
        foreign key (item_id) references items (id),
    constraint item_tags_tags_id_fk
        foreign key (tag_id) references tags (id)
);

create table books
(
    id    BIGINT auto_increment primary key,
    titel varchar(200)  not null,
    place varchar(100)  not null,
    topic varchar(100)  not null,
    note  varchar(500)  null,
    publisher varchar(200) not null,
    isbn      varchar(13)  not null,
    author    varchar(255) not null
);

create table digitals
(
    id    BIGINT auto_increment primary key,
    titel varchar(200)  not null,
    place varchar(100)  not null,
    topic varchar(100)  not null,
    note  varchar(500)  null,
    runtime    varchar(20)  null,
    label      varchar(100) null,
    production varchar(100) null
);

create table actors
(
    id BIGINT auto_increment primary key,
    name varchar(100) not null
);
create table languages
(
    id BIGINT auto_increment primary key,
    name varchar(100) not null
);
create table subtitles
(
    id BIGINT auto_increment primary key,
    language varchar(100) not null
);

create table digitals_actors
(
    item_id BIGINT not null,
    actor_id  BIGINT not null,
    constraint digitals_actors_pk
        primary key (actor_id, item_id),
    constraint digitals_actors_fk_one
        foreign key (item_id) references items (id),
    constraint digitals_actors_fk_two
        foreign key (actor_id) references actors (id)
);

create table digitals_languages
(
    item_id BIGINT not null,
    language_id  BIGINT not null,
    constraint digitals_languages_pk
        primary key (language_id, item_id),
    constraint digitals_languages_fk_one
        foreign key (item_id) references items (id),
    constraint digitals_languages_fk_two
        foreign key (language_id) references languages (id)
);

create table digitals_subtitles
(
    item_id BIGINT not null,
    subtitles_id  BIGINT not null,
    constraint digitals_subtitles_pk
        primary key (subtitles_id, item_id),
    constraint digitals_subtitles_fk_one
        foreign key (item_id) references items (id),
    constraint digitals_subtitles_fk_two
        foreign key (subtitles_id) references subtitles (id)
);

create table donations
(
    id BIGINT auto_increment primary key,
    owner_id BINARY(16) null,
    item_id BIGINT not null,
    amount INTEGER default 1 not null,
    status varchar(10) default 'applied' not null,
    constraint donations_user_fk
        foreign key (owner_id) references users (id)
            on update cascade on delete set null,
    constraint donations_item_fk
        foreign key (item_id) references items (id)
);

create table rentals
(
    item_id             BIGINT                                       not null,
    user_id          binary(16)                                   not null,
    start_date          DATE        default (CURDATE())              not null,
    end_date            DATE        default (DATE_ADD(CURDATE(), INTERVAL 4 Week)) not null,
    status              varchar(10) default 'requested'              not null,
    external_id         binary(16)                                   null,
    id                  BIGINT auto_increment primary key,
    constraint rentals_internals_id
        foreign key (user_id) references users (id),
    constraint rentals_external_id
        foreign key (external_id) references users (id)
);