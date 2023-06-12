ALTER TABLE pchange 
ADD COLUMN is_checked TINYINT NOT NULL DEFAULT 0 AFTER user_id;
UPDATE pchange SET is_checked = 1;
