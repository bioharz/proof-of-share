# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table category (
  id                            integer auto_increment not null,
  title                         varchar(255),
  constraint pk_category primary key (id)
);

create table note (
  id                            integer auto_increment not null,
  title                         varchar(255),
  description                   varchar(255),
  last_edited                   integer not null,
  category_id                   integer not null,
  user_id                       integer,
  constraint pk_note primary key (id)
);

create table user (
  id                            integer auto_increment not null,
  username                      varchar(255),
  password                      varchar(255),
  constraint uq_user_username unique (username),
  constraint pk_user primary key (id)
);

alter table note add constraint fk_note_category_id foreign key (category_id) references category (id) on delete restrict on update restrict;
create index ix_note_category_id on note (category_id);

alter table note add constraint fk_note_user_id foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_note_user_id on note (user_id);


# --- !Downs

alter table note drop constraint if exists fk_note_category_id;
drop index if exists ix_note_category_id;

alter table note drop constraint if exists fk_note_user_id;
drop index if exists ix_note_user_id;

drop table if exists category;

drop table if exists note;

drop table if exists user;

