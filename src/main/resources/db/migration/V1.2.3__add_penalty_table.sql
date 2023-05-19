DROP TABLE IF EXISTS `penalty`;

CREATE TABLE `penalty` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `penalty_type` VARCHAR(20) NOT NULL,
  `message` VARCHAR(100) NULL,
  `start_time` DATETIME NOT NULL,
  `penalty_time` DATETIME NOT NULL,
  `created_at` DATETIME NOT NULL,
  `modified_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_penalty_user_user_id_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_penalty_user_user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `MAIN`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);