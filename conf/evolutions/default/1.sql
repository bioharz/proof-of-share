# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table twitter_bounty_campaign (
  id                            integer auto_increment not null,
  tweet_id                      bigint not null,
  twitter_screen_name           varchar(255),
  satoshi_per_re_tweet          bigint not null,
  satoshi_per_like              bigint not null,
  total_satoshi_to_spend        bigint not null,
  last_edited                   integer not null,
  user_id                       integer,
  constraint pk_twitter_bounty_campaign primary key (id)
);

create table user (
  id                            integer auto_increment not null,
  username                      varchar(255),
  twitter_screen_name           varchar(255),
  password                      varchar(255),
  is_admin                      boolean not null,
  email                         varchar(255) not null,
  constraint uq_user_username unique (username),
  constraint uq_user_twitter_screen_name unique (twitter_screen_name),
  constraint pk_user primary key (id)
);

alter table twitter_bounty_campaign add constraint fk_twitter_bounty_campaign_user_id foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_twitter_bounty_campaign_user_id on twitter_bounty_campaign (user_id);


# --- !Downs

alter table twitter_bounty_campaign drop constraint if exists fk_twitter_bounty_campaign_user_id;
drop index if exists ix_twitter_bounty_campaign_user_id;

drop table if exists twitter_bounty_campaign;

drop table if exists user;

