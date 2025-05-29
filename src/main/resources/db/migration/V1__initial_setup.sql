create table externals
(
    email      varchar(100)                             not null,
    first_name varchar(20)                              not null,
    last_name  varchar(20)                              not null,
    id         BINARY(16) default (UUID_TO_BIN(UUID())) not null primary key
);

create table tags
(
    id   BIGINT auto_increment
        primary key,
    name varchar(20) not null
);

create table items
(
    titel varchar(200)  not null,
    place varchar(100)  not null,
    topic varchar(100)  not null,
    owner binary(16)    null,
    stock int default 1 not null,
    note  varchar(500)  null,
    id    BIGINT auto_increment primary key,
    constraint items_external_id
        foreign key (owner) references externals (id)
            on update cascade on delete set null
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
    publisher varchar(200) not null,
    isbn      varchar(13)  not null,
    author    varchar(255) not null,
    item_id   BIGINT auto_increment primary key,
    constraint books_items_id_fk
        foreign key (item_id) references items (id)
            on update cascade on delete cascade
);

create table digitals
(
    runtime    varchar(20)  null,
    label      varchar(100) null,
    production varchar(100) null,
    item_id    BIGINT auto_increment primary key,
    constraint digitals_items_id_fk
        foreign key (item_id) references items (id)
            on update cascade on delete cascade
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

create table digital_details
(
    id BIGINT not null,
    digital_id  BIGINT not null,
    actor_id    BIGINT null,
    language_id BIGINT null,
    subtitle_id BIGINT null,
    constraint digital_details_pk
        primary key (id),
    constraint digital_details_actors_id_fk
        foreign key (actor_id) references actors (id),
    constraint digital_details_digitals_id_fk
        foreign key (digital_id) references digitals (item_id),
    constraint digital_details_languages_id_fk
        foreign key (language_id) references languages (id),
    constraint digital_details_subtitles_id_fk
        foreign key (subtitle_id) references subtitles (id)
);

create table internals
(
    username            varchar(100)                     not null,
    password            varchar(100)                     not null,
    role                varchar(10) default 'APPLICANT'  not null,
    external_id         binary(16)                       not null primary key,
    constraint users_profiles_id_fk
        foreign key (external_id) references externals (id)
);

create table rentals
(
    item_id             BIGINT                                       not null,
    internal_id          binary(16)                                   not null,
    start_date          DATE        default (CURDATE())              not null,
    end_date            DATE        default (DATE_ADD(CURDATE(), INTERVAL 4 Week)) not null,
    status              varchar(10) default 'requested'              not null,
    external_id         binary(16)                                   null,
    id                  BIGINT auto_increment primary key,
    constraint rentals_internals_id
        foreign key (internal_id) references internals (external_id),
    constraint rentals_external_id
        foreign key (external_id) references externals (id)
);