
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