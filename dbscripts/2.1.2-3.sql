
set sql_safe_updates = 0;

update  exchange_log set is_deleted = 1 WHERE TYPE = 5 AND account_no
IN(648334,717739);

update  account_balance set amount = 0 WHERE unit = 16 AND account_no
IN(648334,717739);
