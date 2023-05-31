DELIMITER //

CREATE TRIGGER prevent_delete_season
BEFORE DELETE ON season
FOR EACH ROW
BEGIN
  IF OLD.start_time < NOW() THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot delete a Season record with startTime in the past';
  END IF;
END //

DELIMITER ;

alter table slot_management add column start_time DATETIME;
alter table slot_management add column end_time DATETIME;

SET SQL_SAFE_UPDATES=0;
DELETE t1
FROM slot_management t1
JOIN (SELECT MAX(created_at) AS max_created_at FROM slot_management) t2
WHERE t1.created_at < t2.max_created_at;
SET SQL_SAFE_UPDATES=0;

UPDATE slot_management
SET start_time = created_at;

alter table slot_management modify start_time DATETIME NOT NULL;