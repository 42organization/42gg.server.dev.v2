ALTER TABLE `MAIN`.`pchange`
    ADD COLUMN `is_checked` BIT(1) NOT NULL DEFAULT 0 AFTER `user_id`;
UPDATE pchange SET is_checked = 1;