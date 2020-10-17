#-------------------------------------------
-- Schema: treecreate
-- Project: https://github.com/Kwandes/treecreate
-- DB version: MySQL 5.7.26
-- Description: Population scripts for tables. The data is used mostly for testing purposes and/or initial setup
#-------------------------------------------

USE treecreate;

-- -----------------------------------------------------
-- Table treecreate.user
-- -----------------------------------------------------
INSERT INTO user (name, email, password, phone_number, street_address, city, postcode, access_level)
VALUES ('tester', 'test@treecreate.dk', '$2a$10$Ad/owORhm9WIA.hDSZ76juwvUuNLicDINElvqGw35uBp10m/ta3Um',
        '12345678', 'Yeetgade 69', 'Copenhagen', '69', 0),
       ('admin', 'sudo@treecreate.dk', '$2a$10$ps42RQxeY6KOVIHYPnA3Su4Rr.ZRSRNAX4uhpqYyDFnLNPsnvqFaa',
        '0101010101', 'Aboozegade 69', 'Copenhagen', '420', 10);