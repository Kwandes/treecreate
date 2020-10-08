#-------------------------------------------
-- Schema: treecreate
-- Project: https://github.com/Kwandes/treecreate
-- DB version: MySQL 5.7.26
-- Description: Creation scripts for all the tables
#-------------------------------------------

DROP SCHEMA IF EXISTS treecreate;
CREATE SCHEMA IF NOT EXISTS treecreate;
USE treecreate;


-- Contains data for users that access the website
CREATE TABLE IF NOT EXISTS treecreate.user
(
    id           INT AUTO_INCREMENT NOT NULL,
    username     VARCHAR(30)        NOT NULL,
    password     VARCHAR(60)        NOT NULL,
    email        VARCHAR(50)        NOT NULL,
    access_level INT DEFAULT 0,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uc_username UNIQUE (username)
);

-- Contains data for email newsletters customers have submitted
CREATE TABLE IF NOT EXISTS treecreate.newsletterEmail
(
    id           INT AUTO_INCREMENT NOT NULL,
    timePlusDate VARCHAR(255)       NOT NULL,
    email        VARCHAR(255)       NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

-- Contains changes done to the tables in this schema. Used by the triggers
CREATE TABLE IF NOT EXISTS treecreate.log
(
    id         INT AUTO_INCREMENT NOT NULL,
    user_id    VARCHAR(64),
    action     VARCHAR(10),
    table_name VARCHAR(15),
    log_time   DATETIME(6),
    data       TEXT,
    CONSTRAINT ph_log PRIMARY KEY (id)
);