USE `vinplay_minigame`;
DROP procedure IF EXISTS `vinplay_minigame`.`save_result_tai_xiu_md5`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `save_result_tai_xiu_md5`(IN reference_id BIGINT,
IN result INT(11),
IN dice1 TINYINT(4),
IN dice2 TINYINT(4),
IN dice3 TINYINT(4),
IN total_tai BIGINT,
IN total_xiu BIGINT,
IN num_bet_tai INT(11),
IN num_bet_xiu INT(11),
IN total_prize BIGINT,
IN total_refund_tai BIGINT,
IN total_refund_xiu BIGINT,
IN total_revenue BIGINT,
IN money_type TINYINT(4))
BEGIN
INSERT INTO result_tai_xiu_md5 (`reference_id`, `result`, `dice1`, `dice2`, `dice3`, `total_tai`, `total_xiu`, `num_bet_tai`, `num_bet_xiu`, `total_prize`, `total_refund_tai`, `total_refund_xiu`, `total_revenue`, `money_type`)
VALUES(reference_id, result, dice1, dice2, dice3, total_tai, total_xiu, num_bet_tai,  num_bet_xiu, total_prize, total_refund_tai, total_refund_xiu, total_revenue, money_type);
END ;;
DELIMITER ;


-- -------------------------------------------------------------------


USE `vinplay_minigame`;
DROP procedure IF EXISTS `vinplay_minigame`.`save_transaction_tai_xiu_md5`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `save_transaction_tai_xiu_md5`(IN reference_id BIGINT,
IN user_id INT(11),
IN user_name VARCHAR(45),
IN bet_value BIGINT,
IN bet_side TINYINT(4),
IN total_prize BIGINT,
IN total_refund BIGINT,
IN money_type TINYINT(4))
BEGIN
  DECLARE total_exchange BIGINT;
    IF (total_prize > 0) THEN
    IF (money_type = 1) THEN
      SET total_exchange = total_prize * 98 / 198;
ELSE
      SET total_exchange = total_prize * 95 / 195;
END IF;
ELSE
    SET total_exchange = -(bet_value - total_refund);
END IF;
INSERT INTO transaction_tai_xiu_md5 (`reference_id`, `user_id`, `user_name`, `bet_value`, `bet_side`, `total_prize`, `total_refund`, `total_exchange`, `money_type`)
VALUES(reference_id, user_id, user_name, bet_value, bet_side, total_prize, total_refund, total_exchange, money_type);
END ;;

-- -------------------------------------------------------------------

USE `vinplay_minigame`;
DROP procedure IF EXISTS `vinplay_minigame`.`save_transaction_detail_tai_xiu_md5`;

DELIMITER $$
CREATE DEFINER=`root`@`%` PROCEDURE `save_transaction_detail_tai_xiu_md5`(IN reference_id BIGINT,
    IN transaction_code VARCHAR(45),
    IN user_id INT,
    IN user_name VARCHAR(45),
    IN bet_value BIGINT,
    IN bet_side TINYINT(4),
    IN prize BIGINT,
    IN refund BIGINT,
    IN input_time INT,
    IN money_type TINYINT(4))
BEGIN
  SET total = (SELECT count(*) from transaction_detail_tai_xiu);
  if total > 100 THEN
DELETE FROM transaction_detail_tai_xiu_md5 WHERE id = (SELECT id FROM vinplay_minigame.transaction_detail_tai_xiu order by id ASC limit 1);
end if;
INSERT INTO transaction_detail_tai_xiu_md5 (`reference_id`, `transaction_code`, `user_id`, `user_name`, `bet_value`, `bet_side`, `prize`, `refund`, `input_time`, `money_type`)
VALUES(reference_id, transaction_code, user_id, user_name, bet_value, bet_side, prize, refund, input_time, money_type);
END$$

DELIMITER ;;

-- -------------------------------------------------------------------



USE `vinplay_minigame`;
DROP procedure IF EXISTS `vinplay_minigame`.`update_transaction_tai_xiu_detail_md5`;

DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `update_transaction_tai_xiu_detail_md5`(IN in_transaction_code VARCHAR(45),
    IN prize BIGINT,
    IN refund BIGINT)
BEGIN
UPDATE transaction_detail_tai_xiu_md5 SET `prize` = prize, `refund` = refund
WHERE `transaction_code` = in_transaction_code;
END ;;
DELIMITER ;


-- -------------------------------------------------------------------

USE `vinplay_minigame`;
DROP procedure IF EXISTS `vinplay_minigame`.`tx_update_thanh_du_md5`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `tx_update_thanh_du_md5`(IN user_name VARCHAR(45),
    IN in_number INT,
    IN total_betting BIGINT,
    IN current_reference_id BIGINT,
    IN in_references VARCHAR(512),
    IN in_type TINYINT)
BEGIN
  IF (SELECT 1=1 FROM thanh_du_md5
      WHERE thanh_du_md5.user_name = user_name AND thanh_du_md5.type = in_type AND thanh_du_md5.`last_update` >= CURDATE()) THEN
BEGIN
UPDATE thanh_du_md5 SET thanh_du_md5.`number` = in_number, thanh_du.total_betting = total_betting,
                        thanh_du_md5.last_reference = current_reference_id, thanh_du_md5.`references` = in_references
WHERE thanh_du_md5.user_name = user_name AND thanh_du_md5.type = in_type AND thanh_du_md5.`last_update` >= CURDATE();
END;
ELSE
BEGIN
INSERT INTO thanh_du_md5(`user_name`, `number`, `total_betting`, `last_reference`, `references`, `last_update`, `type`)
VALUES(user_name, in_number, total_betting, current_reference_id, in_references, CURRENT_TIMESTAMP(), in_type);
END;
END IF;
END ;;
DELIMITER ;


-- -------------------------------------------------------------------

DROP TABLE IF EXISTS `thanh_du_md5`;
CREATE TABLE `thanh_du_md5` (
                                `id` int(11) NOT NULL AUTO_INCREMENT,
                                `user_name` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
                                `number` int(11) DEFAULT '1',
                                `total_betting` bigint(20) DEFAULT NULL,
                                `last_reference` bigint(20) DEFAULT NULL,
                                `references` varchar(512) CHARACTER SET utf8 DEFAULT NULL,
                                `last_update` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                `type` tinyint(4) DEFAULT NULL,
                                PRIMARY KEY (`id`) USING BTREE,
                                KEY `username_index` (`user_name`) USING BTREE,
                                KEY `type_index` (`type`) USING BTREE,
                                KEY `last_update_index` (`last_update`) USING BTREE,
                                KEY `update_index` (`user_name`,`last_update`,`type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=73193 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC;
SELECT * FROM vinplay_minigame.thanh_du;


-- -------------------------------------------------------------------

USE `vinplay_minigame`;
DROP procedure IF EXISTS `vinplay_minigame`.`update_pot_md5`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `update_pot_md5`(IN pot_name VARCHAR(45),
    IN new_value BIGINT)
BEGIN
UPDATE minigame_pots SET `value` = new_value WHERE minigame_pots.`pot_name` = pot_name;
END ;;
DELIMITER ;


-- -------------------------------------------------------------------

DROP TABLE IF EXISTS `user_rut_loc_md5`;


CREATE TABLE `user_rut_loc_md5` (
                                    `id` int(11) NOT NULL AUTO_INCREMENT,
                                    `user_name` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
                                    `so_lan_rut` int(11) DEFAULT NULL,
                                    `last_update` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                    PRIMARY KEY (`id`) USING BTREE,
                                    UNIQUE KEY `user_name_UNIQUE` (`user_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=345494 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC;


-- -------------------------------------------------------------------


USE `vinplay_minigame`;
DROP procedure IF EXISTS `vinplay_minigame`.`update_luot_rut_loc_md5`;

DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `update_luot_rut_loc_md5`(IN user_name VARCHAR(45),
    IN luot_rut INT)
BEGIN
INSERT INTO user_rut_loc_md5(`user_name`, `so_lan_rut`) VALUES (user_name, luot_rut)
    ON DUPLICATE KEY
UPDATE user_rut_loc.so_lan_rut = user_rut_loc.so_lan_rut + luot_rut, user_rut_loc.last_update = CURRENT_TIMESTAMP();
END ;;
DELIMITER ;

-- -------------------------------------------------------------------

USE `vinplay_minigame`;
DROP procedure IF EXISTS `vinplay_minigame`.`update_fund`;

DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `update_fund`(IN fund_name VARCHAR(45),
    IN new_value BIGINT)
BEGIN
UPDATE minigame_funds SET minigame_funds.`value` = new_value WHERE minigame_funds.`fund_name` = fund_name;
END ;;
DELIMITER ;


-- -------------------------------------------------------------------

DROP TABLE IF EXISTS `transaction_tai_xiu_md5`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction_tai_xiu_md5` (
                                           `id` int(11) NOT NULL AUTO_INCREMENT,
                                           `reference_id` bigint(20) DEFAULT NULL,
                                           `user_id` int(11) DEFAULT NULL,
                                           `user_name` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
                                           `bet_value` bigint(20) DEFAULT NULL,
                                           `bet_side` tinyint(4) DEFAULT NULL,
                                           `total_prize` bigint(20) DEFAULT NULL,
                                           `total_refund` bigint(20) DEFAULT NULL,
                                           `total_exchange` bigint(20) DEFAULT '0',
                                           `money_type` tinyint(4) DEFAULT NULL,
                                           `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                           PRIMARY KEY (`id`) USING BTREE,
                                           KEY `reference_index` (`reference_id`) USING BTREE,
                                           KEY `user_name_index` (`user_name`) USING BTREE,
                                           KEY `timestamp_index` (`timestamp`) USING BTREE,
                                           KEY `money_type_index` (`money_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC;



-- -------------------------------------------------------------------


DROP TABLE IF EXISTS `transaction_detail_tai_xiu_md5`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction_detail_tai_xiu_md5` (
                                                  `id` int(11) NOT NULL AUTO_INCREMENT,
                                                  `reference_id` bigint(20) DEFAULT NULL,
                                                  `transaction_code` varchar(120) CHARACTER SET utf8 DEFAULT NULL,
                                                  `user_id` int(11) DEFAULT NULL,
                                                  `user_name` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
                                                  `bet_value` bigint(20) DEFAULT NULL,
                                                  `bet_side` tinyint(4) DEFAULT NULL,
                                                  `prize` bigint(20) DEFAULT NULL,
                                                  `refund` bigint(20) DEFAULT NULL,
                                                  `input_time` int(11) DEFAULT NULL,
                                                  `money_type` tinyint(4) DEFAULT NULL,
                                                  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                                  PRIMARY KEY (`id`) USING BTREE,
                                                  KEY `reference_index` (`reference_id`) USING BTREE,
                                                  KEY `timestampt_index` (`timestamp`) USING BTREE,
                                                  KEY `user_name_index` (`user_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC;



-- -------------------------------------------------------------------

USE `vinplay_minigame`;
DROP procedure IF EXISTS `vinplay_minigame`.`tx_get_top_win_md5`;

DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `tx_get_top_win_md5`(IN money_type TINYINT)
BEGIN
  SET @min_id_today = (SELECT `value` FROM key_value WHERE updated_at = CURDATE() AND `key` = CONCAT('taixiu_', money_type));
    IF ISNULL(@min_id_today) THEN
UPDATE key_value SET `value` = (SELECT min(id) FROM transaction_tai_xiu WHERE `timestamp` >= CURDATE()
                                                                          AND transaction_tai_xiu.money_type = money_type), updated_at = CURDATE() WHERE `key` = CONCAT('taixiu_', money_type);
SET @min_id_today = (SELECT `value` FROM key_value WHERE updated_at = CURDATE() AND `key` = CONCAT('taixiu_', money_type));
END IF;

SELECT user_name, SUM(total_exchange) as money
FROM transaction_tai_xiu
WHERE id >= @min_id_today AND transaction_tai_xiu.money_type = money_type
GROUP BY user_name
HAVING sum(total_exchange) > 0
ORDER BY money DESC
    LIMIT 0, 10;
END ;;


-- -------------------------------------------------------------------

USE `vinplay_minigame`;
DROP procedure IF EXISTS `vinplay_minigame`.`tx_get_lich_su_giao_dich_md5`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `tx_get_lich_su_giao_dich_md5`(IN nickname VARCHAR(45),
    IN page_number INT,
    IN money_type TINYINT(4))
BEGIN
  declare num_start int;
    declare num_end int;
    set num_start = (page_number - 1) * 10;
    set num_end = page_number * 10;
SELECT tran_tx.reference_id, tran_tx.user_id, tran_tx.user_name, tran_tx.bet_value, tran_tx.bet_side,
       tran_tx.total_prize, tran_tx.total_refund, tran_tx.`timestamp`, result_tx.dice1, result_tx.dice2, result_tx.dice3, tran_tx.total_exchange
FROM
    (SELECT * FROM transaction_tai_xiu_md5
     WHERE user_name = nickname AND transaction_tai_xiu.money_type = money_type
     ORDER BY id DESC
         LIMIT num_start, num_end) as tran_tx
        INNER JOIN
    (SELECT * FROM result_tai_xiu_md5 WHERE result_tai_xiu.money_type = money_type) as result_tx
    ON tran_tx.reference_id = result_tx.reference_id
ORDER BY tran_tx.reference_id DESC
    LIMIT 0, 10;
END ;;


-- -------------------------------------------------------------------
USE `vinplay_minigame`;
DROP procedure IF EXISTS `vinplay_minigame`.`tx_count_lich_su_giao_dich_md5`;

DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `tx_count_lich_su_giao_dich_md5`(IN nickname VARCHAR(45),
    IN money_type TINYINT,
    OUT total_records INT)
BEGIN
SELECT count(*) INTO total_records FROM transaction_tai_xiu_md5
WHERE user_name = nickname AND transaction_tai_xiu.money_type = money_type;
END ;;

-- -------------------------------------------------------------------

USE `vinplay_minigame`;
DROP procedure IF EXISTS `vinplay_minigame`.`tx_get_chi_tiet_phien_md5`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `tx_get_chi_tiet_phien_md5`(IN reference_id BIGINT,
    IN money_type TINYINT(4))
BEGIN
SELECT * FROM tx_get_chi_tiet_phien_md5
WHERE transaction_detail_tai_xiu.reference_id = reference_id AND transaction_detail_tai_xiu.money_type = money_type
ORDER BY `timestamp` ASC;
END ;;


-- -------------------------------------------------------------------
USE `vinplay_minigame`;
DROP procedure IF EXISTS `vinplay_minigame`.`tx_get_top_thanh_du_md5`;

DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `tx_get_top_thanh_du_md5`(IN start_time VARCHAR(45),
    IN end_time VARCHAR(45),
    IN in_type TINYINT)
BEGIN
SELECT * FROM thanh_du
WHERE start_time <= thanh_du.last_update AND thanh_du.last_update <= end_time AND thanh_du.`type` = in_type
ORDER BY thanh_du.`number` DESC, thanh_du.total_betting DESC, thanh_du.last_update ASC
    LIMIT 0,10;
END ;;
DELIMITER ;



