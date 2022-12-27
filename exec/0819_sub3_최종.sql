drop database hamgo;
create database hamgo; 

use hamgo;
show tables;
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema hamgo
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema hamgo
-- -----------------------------------------------------

-- show tables;
-- drop table badge;

-----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `member` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `uid` VARCHAR(25) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `nickname` VARCHAR(45) NULL DEFAULT NULL,
  `pimg` VARCHAR(200) NULL DEFAULT NULL,
  `manner` INT DEFAULT 0,
  `discord` VARCHAR(45) NULL DEFAULT NULL,
  `authority` VARCHAR(45) NOT NULL,
  `create_date` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uid_UNIQUE` (`uid` ASC) ,
  UNIQUE INDEX `nickname_UNIQUE` (`nickname` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `board`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `board` (
  `bid` BIGINT NOT NULL AUTO_INCREMENT,
  `contents` VARCHAR(1000) NULL DEFAULT NULL,
  `create_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `likes` INT NULL DEFAULT '0',
  `hashtags` VARCHAR(500),
  `share` INT NULL DEFAULT '0',
  `uid` VARCHAR(25) NOT NULL,
  PRIMARY KEY (`bid`),
  INDEX `board_foreign_key_idx` (`uid` ASC),
  CONSTRAINT `board_foreign_key`
    FOREIGN KEY (`uid`)
    REFERENCES `member` (`uid`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `board_like_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `board_like_member` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `bid` BIGINT NOT NULL,
  `uid` VARCHAR(25) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `boadr_like_foreign_key2_idx` (`bid` ASC),
  INDEX `board_like_foreign_key_idx` (`uid` ASC),
  CONSTRAINT `boadr_like_foreign_key2`
    FOREIGN KEY (`bid`)
    REFERENCES `board` (`bid`)
    ON DELETE CASCADE,
  CONSTRAINT `board_like_foreign_key`
    FOREIGN KEY (`uid`)
    REFERENCES `member` (`uid`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `file`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `file` (
  `no` BIGINT NOT NULL AUTO_INCREMENT,
  `file_name` VARCHAR(200) NULL DEFAULT NULL,
  `file_base_url` VARCHAR(200) NULL DEFAULT NULL,
  `file_size` VARCHAR(45) NULL DEFAULT NULL,
  `bid` BIGINT NOT NULL,
  PRIMARY KEY (`no`),
  UNIQUE INDEX `file_name_UNIQUE` (`file_name` ASC),
  INDEX `file_foreign_key_idx` (`bid` ASC),
  CONSTRAINT `file_foreign_key`
    FOREIGN KEY (`bid`)
    REFERENCES `board` (`bid`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;


-- -----------------------------------------------------
-- Table `follower`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `follower` (
  `fid` BIGINT NOT NULL AUTO_INCREMENT,
  `to_id` VARCHAR(25) NOT NULL,
  `from_id` VARCHAR(25) NOT NULL,
  PRIMARY KEY (`fid`),
  INDEX `from_foreign_key_idx` (`from_id` ASC) ,
  INDEX `to_foreign_key_idx` (`to_id` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
;


-- -----------------------------------------------------
-- Table `following`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `following` (
  `fid` BIGINT NOT NULL AUTO_INCREMENT,
  `from_id` VARCHAR(25) NOT NULL,
  `to_id` VARCHAR(25) NOT NULL,
  PRIMARY KEY (`fid`),
  INDEX `from_foreign_key_idx` (`from_id` ASC),
  INDEX `from_foreign_key_idx1` (`to_id` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

-- -----------------------------------------------------
-- Table `reply`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `reply` (
  `rid` BIGINT NOT NULL AUTO_INCREMENT,
  `bid` BIGINT NOT NULL,
  `uid` VARCHAR(25) NOT NULL,
  `content` VARCHAR(45) NULL DEFAULT NULL,
  `cnt` INT NULL DEFAULT '0',
  `reg_date` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`rid`),
  INDEX `reply_foreign_key_idx` (`uid` ASC),
  INDEX `reply_foreign_key2_idx` (`bid` ASC),
  CONSTRAINT `reply_foreign_key`
    FOREIGN KEY (`uid`)
    REFERENCES `member` (`uid`)
    ON DELETE CASCADE,
  CONSTRAINT `reply_foreign_key2`
    FOREIGN KEY (`bid`)
    REFERENCES `board` (`bid`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE IF NOT EXISTS `badge` (
  `no` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(25) NOT NULL,
  `type` VARCHAR(45) NOT NULL,
  `lowcut` INT(11) NOT NULL,
  PRIMARY KEY (`no`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `game` (
  `gid` INT NOT NULL AUTO_INCREMENT,
  `game_name` VARCHAR(25) NOT NULL,
  `num` int NOT NULL,
  PRIMARY KEY (`gid`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

insert into game(`game_name`, `num`) values('LeagueOfLegends', 2);
insert into game(`game_name`, `num`) values('LeagueOfLegends', 5);
insert into game(`game_name`, `num`) values('EternalReturn', 3);
insert into game(`game_name`, `num`) values('AmongUs', 5);
insert into game(`game_name`, `num`) values('AmongUs', 10);
insert into game(`game_name`, `num`) values('PayDay3', 4);
insert into game(`game_name`, `num`) values('LostArk', 4);
insert into game(`game_name`, `num`) values('DeepRockGalactic', 4);
INSERT INTO badge VALUES (1,'아기매너','manner',5),(2,'매너왕','manner',10),(3,'댓글왕','reply',3),(4,'글왕','board',3);


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- show tables;
select * from badge;
