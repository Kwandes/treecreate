#-------------------------------------------
-- Schema: treecreate
-- Project: https://github.com/Kwandes/treecreate
-- DB version: MySQL 5.7.26
-- Description: Creation scripts for all the triggers. The triggers log all of the table changes to treecreate.log
#-------------------------------------------

-- -----------------------------------------------------
-- Table treecreate.user
-- -----------------------------------------------------
CREATE TRIGGER tr_treecreate_user_ins
    AFTER INSERT
    ON treecreate.user
    FOR EACH ROW
    INSERT INTO treecreate.log(user_id, action, table_name, log_time, data)
    VALUES (USER(),
            'insert',
            'user',
            CURRENT_TIME(6),
            CONCAT(NEW.username, ' | ', NEW.password, ' | ', NEW.email, ' | ', NEW.access_level));

CREATE TRIGGER tr_treecreate_user_upd
    BEFORE UPDATE
    ON treecreate.user
    FOR EACH ROW
BEGIN
    INSERT INTO treecreate.log(user_id, action, table_name, log_time, data)
    VALUES (USER(),
            'update',
            'user',
            CURRENT_TIME(6),
            CONCAT(NEW.username, ' | ', NEW.password, ' | ', NEW.email, ' | ', NEW.access_level));
END;

CREATE TRIGGER tr_treecreate_user_del
    BEFORE DELETE
    ON treecreate.user
    FOR EACH ROW
BEGIN
    INSERT INTO treecreate.log(user_id, action, table_name, log_time, data)
    VALUES (USER(),
            'delete',
            'user',
            CURRENT_TIME(6),
            CONCAT(OLD.username, ' | ', OLD.password, ' | ', OLD.email, ' | ', OLD.access_level));
END;

-- -----------------------------------------------------
-- Table treecreate.newsletterEmail
-- -----------------------------------------------------
CREATE TRIGGER tr_treecreate_newsletterEmail_ins
    AFTER INSERT
    ON treecreate.newsletterEmail
    FOR EACH ROW
    INSERT INTO treecreate.log(user_id, action, table_name, log_time, data)
    VALUES (USER(),
            'insert',
            'newsletterEmail',
            CURRENT_TIME(6),
            CONCAT(NEW.timePlusDate, ' | ', NEW.email));

CREATE TRIGGER tr_treecreate_newsletterEmail_upd
    BEFORE UPDATE
    ON treecreate.newsletterEmail
    FOR EACH ROW
BEGIN
    INSERT INTO treecreate.log(user_id, action, table_name, log_time, data)
    VALUES (USER(),
            'update',
            'newsletterEmail',
            CURRENT_TIME(6),
            CONCAT(NEW.timePlusDate, ' | ', NEW.email));
END;

CREATE TRIGGER tr_treecreate_newsletterEmail_del
    BEFORE DELETE
    ON treecreate.newsletterEmail
    FOR EACH ROW
BEGIN
    INSERT INTO treecreate.log(user_id, action, table_name, log_time, data)
    VALUES (USER(),
            'delete',
            'newsletterEmail',
            CURRENT_TIME(6),
            CONCAT(OLD.timePlusDate, ' | ', OLD.email));
END;