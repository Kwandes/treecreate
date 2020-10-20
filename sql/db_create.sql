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
CREATE TABLE IF NOT EXISTS treecreate.user (
    id             INT AUTO_INCREMENT NOT NULL,
    name           VARCHAR(80)        NOT NULL,
    email          VARCHAR(254)       NOT NULL,
    password       VARCHAR(60)        NOT NULL,
    phone_number   VARCHAR(15)        NOT NULL,
    street_address VARCHAR(99)        NOT NULL,
    city           VARCHAR(50)        NOT NULL,
    postcode       VARCHAR(15)        NOT NULL,
    access_level   INT DEFAULT 0,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uc_email UNIQUE (email)
);

-- Contains data for email newsletters customers have submitted
CREATE TABLE IF NOT EXISTS treecreate.newsletterEmail (
    id           INT AUTO_INCREMENT NOT NULL,
    timePlusDate VARCHAR(255)       NOT NULL,
    email        VARCHAR(255),
    CONSTRAINT pk_user PRIMARY KEY (id)
);

-- Contains a family tree design and other metadata related to it
CREATE TABLE IF NOT EXISTS treecreate.family_tree (
    id             INT AUTO_INCREMENT NOT NULL,
    time_plus_date VARCHAR(255)       NOT NULL,
    owner_id       VARCHAR(25),
    design         TEXT,
    CONSTRAINT pk_familyTreeDesign PRIMARY KEY (id)
);

-- Contains changes done to the tables in this schema. Used by the triggers
CREATE TABLE IF NOT EXISTS treecreate.log (
    id         INT AUTO_INCREMENT NOT NULL,
    user_id    VARCHAR(64),
    action     VARCHAR(10),
    table_name VARCHAR(15),
    log_time   DATETIME(6),
    data       TEXT,
    CONSTRAINT ph_log PRIMARY KEY (id)
);