set sql_safe_updates = 0;
update qbao_schema.wallet_account set receive_number = 1 where id > 0 and receive_number is null;
update qbao_schema.account_balance set amount =0  where id >0 and unit = 16;