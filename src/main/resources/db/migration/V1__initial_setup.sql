create table users
(
    email      varchar(100)                             not null,
    first_name varchar(20)                              not null,
    last_name  varchar(20)                              not null,
    role                varchar(10) default 'EXTERNAL'  not null,
    password            varchar(100)                    null,
    id         BINARY(16) default (UUID_TO_BIN(UUID())) not null primary key,
    constraint role_check check (
        (role <> 'EXTERNAL' AND Password IS NOT NULL) OR
        (role = 'EXTERNAL' AND Password IS NULL)
    ),
    constraint users_email_unique
        unique (email)
);

create table tags
(
    id   BIGINT auto_increment primary key,
    name varchar(20) not null
);

create table items
(
    id              BIGINT auto_increment primary key,
    titel           varchar(200)  not null,
    place           varchar(100)  null,
    topic           varchar(100)  not null,
    note            varchar(500)  null,
    stock           SMALLINT not null,
    rental_count    SMALLINT not null,
    state           varchar(10) default 'PUBLIC' not null
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
    id    BIGINT not null,
    publisher varchar(200) not null,
    isbn      varchar(13)  not null,
    author    varchar(255) not null,
    constraint books_item_id_fk
        foreign key (id) references items (id),
    constraint books_id_pk
        primary key (id)
);

create table digitals
(
    id    BIGINT not null,
    runtime    varchar(20)  null,
    label      varchar(100) null,
    production varchar(100) null,
    constraint digitals_item_id_fk
        foreign key (id) references items (id),
    constraint digitals_id_pk
        primary key (id)
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
    digitals_id BIGINT not null,
    actor_id  BIGINT not null,
    constraint digitals_actors_pk
        primary key (actor_id, digitals_id),
    constraint digitals_actors_fk_one
        foreign key (digitals_id) references digitals (id),
    constraint digitals_actors_fk_two
        foreign key (actor_id) references actors (id)
);

create table digitals_languages
(
    digitals_id BIGINT not null,
    language_id  BIGINT not null,
    constraint digitals_languages_pk
        primary key (language_id, digitals_id),
    constraint digitals_languages_fk_one
        foreign key (digitals_id) references digitals (id),
    constraint digitals_languages_fk_two
        foreign key (language_id) references languages (id)
);

create table digitals_subtitles
(
    digitals_id BIGINT not null,
    subtitles_id  BIGINT not null,
    constraint digitals_subtitles_pk
        primary key (subtitles_id, digitals_id),
    constraint digitals_subtitles_fk_one
        foreign key (digitals_id) references digitals (id),
    constraint digitals_subtitles_fk_two
        foreign key (subtitles_id) references subtitles (id)
);

create table donations
(
    id BIGINT auto_increment primary key,
    owner_id BINARY(16) null,
    item_id BIGINT not null,
    donation_condition varchar(10) default 'USED' not null,
    status varchar(10) default 'APPLIED' not null,
    donation_date date default (CURDATE()) not null,
    constraint donations_user_fk
        foreign key (owner_id) references users (id)
            on update cascade on delete set null,
    constraint donations_item_fk
        foreign key (item_id) references items (id)
);

create table rentals
(
    id                  BIGINT auto_increment primary key,
    donation_id         BIGINT                                                      not null,
    start_date          DATE        default (CURDATE())                             not null,
    end_date            DATE        default (DATE_ADD(CURDATE(), INTERVAL 4 Week))  not null,
    status              varchar(10) default 'REQUESTED'                             not null,
    user_id             binary(16)                                                  not null,
    external_id         binary(16)                                                  null,
    constraint rentals_donation_id
        foreign key (donation_id) references donations (id),
    constraint rentals_external_id
        foreign key (external_id) references users (id),
    constraint rentals_donation_id_unique
        unique (donation_id)
);