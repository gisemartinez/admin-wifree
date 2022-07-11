# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table address (
  id                            bigint auto_increment not null,
  owner_id                      bigint not null,
  street                        varchar(255),
  number                        integer,
  other                         varchar(255),
  city                          varchar(255),
  state                         varchar(255),
  country                       varchar(255),
  zip_code                      varchar(255),
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint uq_address_owner_id unique (owner_id),
  constraint pk_address primary key (id)
);

create table admin (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  email                         varchar(255) not null,
  password                      varchar(255),
  last_connection               timestamp,
  online                        boolean default false not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_admin primary key (id)
);

create table analytics_query_filter (
  id                            bigint auto_increment not null,
  portal_id                     bigint not null,
  name                          varchar(255) not null,
  description                   varchar(255),
  time_period_type              varchar(8),
  time_period                   varchar(255),
  gender                        varchar(9),
  age_range                     varchar(255),
  visits_amount                 varchar(255),
  visitor_address               varchar(255),
  days_of_the_week              array,
  hours                         varchar(255),
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint ck_analytics_query_filter_time_period_type check ( time_period_type in ('ABSOLUTE','RELATIVE','ERROR','NONE')),
  constraint ck_analytics_query_filter_gender check ( gender in ('Undefined','Male','Female','Error','Other')),
  constraint pk_analytics_query_filter primary key (id)
);

create table field (
  id                            bigint auto_increment not null,
  survey_id                     bigint not null,
  type                          varchar(255),
  config                        clob,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_field primary key (id)
);

create table field_answer (
  id                            bigint auto_increment not null,
  field_id                      bigint not null,
  user_id                       bigint not null,
  answer                        varchar(255) not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint uq_field_answer_field_id_user_id unique (field_id,user_id),
  constraint pk_field_answer primary key (id)
);

create table network_user (
  id                            bigint auto_increment not null,
  portal_id                     bigint not null,
  name                          varchar(255),
  email                         varchar(255) not null,
  last_used_macaddress          varchar(255),
  last_connection               timestamp,
  online                        boolean default false not null,
  password                      varchar(255),
  gender                        varchar(9),
  age                           integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint ck_network_user_gender check ( gender in ('Undefined','Male','Female','Error','Other')),
  constraint pk_network_user primary key (id)
);

create table network_user_connection_log (
  id                            bigint auto_increment not null,
  portal_id                     bigint not null,
  network_user_id               bigint not null,
  connection_start_date         timestamp,
  connection_end_date           timestamp,
  user_device_macaddress        varchar(255),
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_network_user_connection_log primary key (id)
);

create table network_user_macaddress (
  id                            bigint auto_increment not null,
  network_user_id               bigint not null,
  mac_address                   varchar(255) not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint uq_network_user_macaddress_network_user_id_mac_address unique (network_user_id,mac_address),
  constraint pk_network_user_macaddress primary key (id)
);

create table network_user_social_network_account (
  id                            bigint auto_increment not null,
  network_user_id               bigint not null,
  social_network                integer,
  account_id                    varchar(255),
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint ck_network_user_social_network_account_social_network check ( social_network in (0,1,2,3)),
  constraint pk_network_user_social_network_account primary key (id)
);

create table portal (
  id                            bigint auto_increment not null,
  name                          varchar(255) not null,
  description                   varchar(255),
  account_type                  integer not null,
  owner_id                      bigint not null,
  home_url                      varchar(255),
  facebook_url                  varchar(255),
  twitter_url                   varchar(255),
  google_plus_url               varchar(255),
  instagram_url                 varchar(255),
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint ck_portal_account_type check ( account_type in (0,1,2)),
  constraint uq_portal_owner_id unique (owner_id),
  constraint pk_portal primary key (id)
);

create table portal_app (
  id                            bigint auto_increment not null,
  portal_id                     bigint not null,
  type                          integer,
  name                          varchar(255),
  enabled                       boolean default false not null,
  config                        clob,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint ck_portal_app_type check ( type in (0,1,2,3,4,5,6,7)),
  constraint pk_portal_app primary key (id)
);

create table portal_login_configuration (
  id                            bigint auto_increment not null,
  portal_id                     bigint not null,
  login_method                  integer,
  is_social_login               boolean default false not null,
  enabled                       boolean default false not null,
  redirect_url                  varchar(255),
  keys                          clob,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint ck_portal_login_configuration_login_method check ( login_method in (0,1,2,3,4,5)),
  constraint uq_portal_login_configuration_portal_id_login_method unique (portal_id,login_method),
  constraint pk_portal_login_configuration primary key (id)
);

create table portal_network_configuration (
  id                            bigint auto_increment not null,
  portal_id                     bigint not null,
  connection_timeout            integer,
  login_method                  integer,
  enable_bans                   boolean default false not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint ck_portal_network_configuration_login_method check ( login_method in (0,1,2,3,4,5)),
  constraint pk_portal_network_configuration primary key (id)
);

create table survey (
  id                            bigint auto_increment not null,
  portal_id                     bigint not null,
  title                         varchar(255),
  enabled                       boolean default false not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_survey primary key (id)
);

alter table address add constraint fk_address_owner_id foreign key (owner_id) references network_user (id) on delete restrict on update restrict;

alter table analytics_query_filter add constraint fk_analytics_query_filter_portal_id foreign key (portal_id) references portal (id) on delete restrict on update restrict;
create index ix_analytics_query_filter_portal_id on analytics_query_filter (portal_id);

alter table field add constraint fk_field_survey_id foreign key (survey_id) references survey (id) on delete restrict on update restrict;
create index ix_field_survey_id on field (survey_id);

alter table field_answer add constraint fk_field_answer_field_id foreign key (field_id) references field (id) on delete restrict on update restrict;
create index ix_field_answer_field_id on field_answer (field_id);

alter table field_answer add constraint fk_field_answer_user_id foreign key (user_id) references network_user (id) on delete restrict on update restrict;
create index ix_field_answer_user_id on field_answer (user_id);

alter table network_user add constraint fk_network_user_portal_id foreign key (portal_id) references portal (id) on delete restrict on update restrict;
create index ix_network_user_portal_id on network_user (portal_id);

alter table network_user_connection_log add constraint fk_network_user_connection_log_portal_id foreign key (portal_id) references portal (id) on delete restrict on update restrict;
create index ix_network_user_connection_log_portal_id on network_user_connection_log (portal_id);

alter table network_user_connection_log add constraint fk_network_user_connection_log_network_user_id foreign key (network_user_id) references network_user (id) on delete restrict on update restrict;
create index ix_network_user_connection_log_network_user_id on network_user_connection_log (network_user_id);

alter table network_user_macaddress add constraint fk_network_user_macaddress_network_user_id foreign key (network_user_id) references network_user (id) on delete restrict on update restrict;
create index ix_network_user_macaddress_network_user_id on network_user_macaddress (network_user_id);

alter table network_user_social_network_account add constraint fk_network_user_social_network_account_network_user_id foreign key (network_user_id) references network_user (id) on delete restrict on update restrict;
create index ix_network_user_social_network_account_network_user_id on network_user_social_network_account (network_user_id);

alter table portal add constraint fk_portal_owner_id foreign key (owner_id) references admin (id) on delete restrict on update restrict;

alter table portal_app add constraint fk_portal_app_portal_id foreign key (portal_id) references portal (id) on delete restrict on update restrict;
create index ix_portal_app_portal_id on portal_app (portal_id);

alter table portal_login_configuration add constraint fk_portal_login_configuration_portal_id foreign key (portal_id) references portal (id) on delete restrict on update restrict;
create index ix_portal_login_configuration_portal_id on portal_login_configuration (portal_id);

alter table portal_network_configuration add constraint fk_portal_network_configuration_portal_id foreign key (portal_id) references portal (id) on delete restrict on update restrict;
create index ix_portal_network_configuration_portal_id on portal_network_configuration (portal_id);

alter table survey add constraint fk_survey_portal_id foreign key (portal_id) references portal (id) on delete restrict on update restrict;
create index ix_survey_portal_id on survey (portal_id);


# --- !Downs

alter table address drop constraint if exists fk_address_owner_id;

alter table analytics_query_filter drop constraint if exists fk_analytics_query_filter_portal_id;
drop index if exists ix_analytics_query_filter_portal_id;

alter table field drop constraint if exists fk_field_survey_id;
drop index if exists ix_field_survey_id;

alter table field_answer drop constraint if exists fk_field_answer_field_id;
drop index if exists ix_field_answer_field_id;

alter table field_answer drop constraint if exists fk_field_answer_user_id;
drop index if exists ix_field_answer_user_id;

alter table network_user drop constraint if exists fk_network_user_portal_id;
drop index if exists ix_network_user_portal_id;

alter table network_user_connection_log drop constraint if exists fk_network_user_connection_log_portal_id;
drop index if exists ix_network_user_connection_log_portal_id;

alter table network_user_connection_log drop constraint if exists fk_network_user_connection_log_network_user_id;
drop index if exists ix_network_user_connection_log_network_user_id;

alter table network_user_macaddress drop constraint if exists fk_network_user_macaddress_network_user_id;
drop index if exists ix_network_user_macaddress_network_user_id;

alter table network_user_social_network_account drop constraint if exists fk_network_user_social_network_account_network_user_id;
drop index if exists ix_network_user_social_network_account_network_user_id;

alter table portal drop constraint if exists fk_portal_owner_id;

alter table portal_app drop constraint if exists fk_portal_app_portal_id;
drop index if exists ix_portal_app_portal_id;

alter table portal_login_configuration drop constraint if exists fk_portal_login_configuration_portal_id;
drop index if exists ix_portal_login_configuration_portal_id;

alter table portal_network_configuration drop constraint if exists fk_portal_network_configuration_portal_id;
drop index if exists ix_portal_network_configuration_portal_id;

alter table survey drop constraint if exists fk_survey_portal_id;
drop index if exists ix_survey_portal_id;

drop table if exists address;

drop table if exists admin;

drop table if exists analytics_query_filter;

drop table if exists field;

drop table if exists field_answer;

drop table if exists network_user;

drop table if exists network_user_connection_log;

drop table if exists network_user_macaddress;

drop table if exists network_user_social_network_account;

drop table if exists portal;

drop table if exists portal_app;

drop table if exists portal_login_configuration;

drop table if exists portal_network_configuration;

drop table if exists survey;

