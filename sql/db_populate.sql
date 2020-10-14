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
VALUES ('tester', 'test@treecreate.dk', 'pass', '12345678', 'Yeetgade 69', 'Copenhagen', '69', 0),
       ('admin', 'sudo@treecreate.dk', 'superpass', '0101010101', 'Aboozegade 69', 'Copenhagen', '420', 10);