#-------------------------------------------
-- Schema: treecreate
-- Project: https://github.com/Kwandes/treecreate
-- DB version: MySQL 5.7.26
-- Description: Population scripts for tables. The data is used mostly for testing purposes and/or initial setup
#-------------------------------------------

USE treecreate;

DELETE FROM treecreate.user;

-- -----------------------------------------------------
-- Table treecreate.user
-- -----------------------------------------------------
INSERT INTO user (name, email, password, phone_number, street_address, city, postcode, access_level)
VALUES ('tester', 'test@treecreate.dk', '$2a$12$KwRGRqYeOKkHmMV/pT6i7ukzIwBImi3sbo.T21sow0ci0GKSnkTe.', '12345678', 'Yeetgade 69', 'Copenhagen', '69', 0),
       ('admin', 'sudo@treecreate.dk', '$2a$12$eIsu5K/Ek.4o1.XYVHPKpuV8AH2KuRkdqY4WvWNyACN4V.fqVGAFy', '0101010101', 'Aboozegade 69', 'Copenhagen', '420', 10);