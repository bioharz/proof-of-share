# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table user (
  id                            integer auto_increment not null,
  username                      varchar(255),
  password                      varchar(255),
  is_admin                      boolean not null,
  email                         varchar(255) not null,
  constraint uq_user_username unique (username),
  constraint pk_user primary key (id)
);


# --- !Downs

drop table if exists user;

