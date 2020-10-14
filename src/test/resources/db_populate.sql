-- -------------------------------------------
-- Schema: treecreate
-- Project: https://github.com/Kwandes/treecreate
-- DB version: MySQL 5.7.26
-- Description: Population scripts for tables for testing purposes
-- -------------------------------------------

-- -----------------------------------------------------
-- Table treecreate.user
-- -----------------------------------------------------
INSERT INTO user (id, name, email, password, phone_number, street_address, city, postcode, access_level)
VALUES (1, 'tester', 'test@treecreate.dk', 'pass', '12345678', 'Yeetgade 69', 'Copenhagen', '69', 0),
       (2, 'admin', 'sudo@treecreate.dk', 'superpass', '0101010101', 'Aboozegade 69', 'Copenhagen', '420', 10);