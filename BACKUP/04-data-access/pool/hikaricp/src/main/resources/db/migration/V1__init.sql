DROP TABLE IF EXISTS `USER`;
CREATE TABLE USER (
	id BIGINT GENERATED BY DEFAULT AS IDENTITY,
	username varchar(255) not null,
	password varchar(255) not null
);

insert into user (username, password) values ('飞翔的大白菜', '123456');