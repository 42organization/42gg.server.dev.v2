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