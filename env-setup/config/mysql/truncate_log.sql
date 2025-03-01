
-- Kịch bản được sử dụng để xóa log khi dựng hệ thống mới
-- 1. Lấy thông tin bảng
SELECT table_name AS "Table",
       ROUND(((data_length + index_length) / 1024 / 1024), 2) AS "Size (MB)"
FROM information_schema.TABLES
WHERE table_schema = "cgame"
-- WHERE table_schema = "vinplay"
-- WHERE table_schema = "vinplay_admin"
-- WHERE table_schema = "vinplay_gamebai"
-- WHERE table_schema = "vinplay_minigame"
-- WHERE table_schema = "vinplay_marketing"
ORDER BY (data_length + index_length) DESC;

-- truncate log tables
-- truncate log tables
TRUNCATE `cgame`.`bc_log`;
TRUNCATE `cgame`.`login_start`;
TRUNCATE `cgame`.`bc_ccu`;
TRUNCATE `cgame`.`bc_trans_log`;
TRUNCATE `cgame`.`bc_history`;
TRUNCATE `cgame`.`bc_trans_log`;
TRUNCATE `cgame`.`users`;
TRUNCATE `cgame`.`bc_players`;
TRUNCATE `cgame`.`bc_leaderboard_week`;
TRUNCATE `cgame`.`bc_slot5_histories`;
TRUNCATE `cgame`.`slot5_histories`;
TRUNCATE `cgame`.`bc_leaderboard_month`;
TRUNCATE `cgame`.`bc_announce`;
TRUNCATE `cgame`.`gift_codes`;
TRUNCATE `cgame`.`bc_fish_log`;
TRUNCATE `cgame`.`bc_slot5_glory`;

TRUNCATE `vinplay`.`freeze_money`;
TRUNCATE `vinplay`.`report_money_daily`;
TRUNCATE `vinplay`.`log_tranfer_agent`;

TRUNCATE `vinplay_admin`.`log_loginadmin`;
TRUNCATE `vinplay_admin`.`log_admin`;


TRUNCATE `vinplay_minigame`.`transaction_detail_tai_xiu`;
TRUNCATE `vinplay_minigame`.`transaction_detail_tai_xiu_md5`;
TRUNCATE `vinplay_minigame`.`transaction_tai_xiu`;
TRUNCATE `vinplay_minigame`.`transaction_tai_xiu_md5`;
TRUNCATE `vinplay_minigame`.`transaction_detail_tai_xiu_kubet`;
TRUNCATE `vinplay_minigame`.`transaction_tai_xiu_kubet`;
TRUNCATE `vinplay_minigame`.`thanh_du`;
TRUNCATE `vinplay_minigame`.`result_tai_xiu_md5`;
TRUNCATE `vinplay_minigame`.`result_tai_xiu`;
TRUNCATE `vinplay_minigame`.`result_tai_xiu_kubet`;
TRUNCATE `vinplay_minigame`.`lucky_rotation`;
TRUNCATE `vinplay_minigame`.`user_rut_loc`;
TRUNCATE `vinplay_minigame`.`user_rut_loc`;
TRUNCATE `vinplay_minigame`.`user_rut_loc`;
TRUNCATE `vinplay_minigame`.`user_rut_loc`;



update vinplay_minigame.minigame_funds set value = 0 where id >= 1;


UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '1');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '500000' WHERE (`id` = '224');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '5000000' WHERE (`id` = '225');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '50000000' WHERE (`id` = '226');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '50000000' WHERE (`id` = '223');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '5000000' WHERE (`id` = '222');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '500000' WHERE (`id` = '221');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '50000000' WHERE (`id` = '220');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '5000000' WHERE (`id` = '219');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '500000' WHERE (`id` = '218');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '50000000' WHERE (`id` = '211');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '5000000' WHERE (`id` = '210');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '500000' WHERE (`id` = '209');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '50000000' WHERE (`id` = '214');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '5000000' WHERE (`id` = '213');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '500000' WHERE (`id` = '212');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '50000000' WHERE (`id` = '205');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '5000000' WHERE (`id` = '204');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '500000' WHERE (`id` = '203');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '50000000' WHERE (`id` = '202');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '5000000' WHERE (`id` = '201');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '500000' WHERE (`id` = '200');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '50000000' WHERE (`id` = '199');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '5000000' WHERE (`id` = '198');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '500000' WHERE (`id` = '197');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '194');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '187');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '186');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '185');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '179');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '178');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '177');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '139');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '138');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '137');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '131');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '130');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '129');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '123');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '122');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '121');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '120');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '119');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '118');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '117');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '116');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '115');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '114');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '113');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '112');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '111');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '90');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '89');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '88');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '87');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '86');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '85');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '84');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '83');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '82');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '81');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '80');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '79');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '78');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '77');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '76');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '74');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '73');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '50000000' WHERE (`id` = '72');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '5000000' WHERE (`id` = '71');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '500000' WHERE (`id` = '70');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '500000' WHERE (`id` = '26');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '5000000' WHERE (`id` = '27');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '50000000' WHERE (`id` = '28');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '29');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '30');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '31');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '65');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '66');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '67');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '68');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '0' WHERE (`id` = '69');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '1000000' WHERE (`id` = '60');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '10000000' WHERE (`id` = '61');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '50000000' WHERE (`id` = '62');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '100000000' WHERE (`id` = '63');
UPDATE `vinplay_minigame`.`minigame_pots` SET `value` = '500000000' WHERE (`id` = '64');
