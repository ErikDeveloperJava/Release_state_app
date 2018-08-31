create database realestate_db character set utf8 collate utf8_general_ci;

use realestate_db;

create table user(
  id bigint not null auto_increment primary key ,
  name varchar(255) not null ,
  email varchar(255) not null ,
  password varchar(255) not null ,
  pic_url varchar(255) not null ,
  user_type varchar(255) not null
)engine InnoDB character set utf8 collate utf8_general_ci;

create table listing(
  id bigint not null auto_increment primary key ,
  title varchar(255) not null ,
  description varchar(255) not null ,
  price double not null ,
  bedrooms int not null ,
  bathrooms int not null ,
  area double not null ,
  mls_no varchar(255) not null ,
  listing_type varchar(255) not null ,
  pic_url varchar(255) not null ,
  user_id int not null
)engine InnoDB character set utf8 collate utf8_general_ci;

create table feature(
  id bigint not null auto_increment primary key ,
  name varchar(255) not null
)engine InnoDB character set utf8 collate utf8_general_ci;

create table listing_feature(
  listing_id bigint not null ,
  feature_id bigint not null ,
  foreign key (listing_id) references listing(id) on delete cascade ,
  foreign key (feature_id) references feature(id) on delete cascade
)engine InnoDB character set utf8 collate utf8_general_ci;

create table image(
  id bigint not null auto_increment primary key ,
  url varchar(255) not null ,
  listing_id bigint not null ,
  foreign key (listing_id) references listing(id) on delete cascade
)engine InnoDB character set utf8 collate utf8_general_ci;


insert into feature(name)values
  ('Garden'),
  ('Gym'),
  ('Internet'),
  ('Swimming Pool'),
  ('Window covering'),
  ('Parking'),
  ('School'),
  ('Bank'),
  ('Metro'),
  ('Airport');

insert into user(name, email, password, pic_url, user_type) VALUES
  ('Admin','admin@mail.com','$2a$04$noFFMlVHELB5yLnsZkdIlOTjqFrZLLdIFfKX8HM9/7npqbJzs1S8G','admin.jpg','ADMIN');
