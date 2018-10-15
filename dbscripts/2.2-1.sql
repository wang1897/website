ALTER TABLE `qbao_schema`.`user_group`
CHANGE COLUMN `account_no` `account_no` VARCHAR(10) CHARACTER SET 'utf8' NOT NULL ;

ALTER TABLE `qbao_schema`.`exchange_log`
ADD INDEX `INDEX_TXID` (`transaction_hash` ASC);


update qbao_schema.wallet_account set account_no = '0001',share_code='AAAB' where account_no = '009804';
update qbao_schema.wallet_account set account_no = '0002',share_code='AAAC'  where account_no = '94945111';
update qbao_schema.wallet_account set account_no = '0003',share_code='AAAD' where account_no = '25276125';
update qbao_schema.wallet_account set account_no = '0004',share_code='AAAE' where account_no = '12724196';
update qbao_schema.wallet_account set account_no = '0005',share_code='AAAF' where account_no = '82102515';


update qbao_schema.wallet_account set rong_token = null where account_no = '0001';
update qbao_schema.wallet_account set rong_token = null where account_no = '0002';
update qbao_schema.wallet_account set rong_token = null where account_no = '0003';
update qbao_schema.wallet_account set rong_token = null where account_no = '0004';
update qbao_schema.wallet_account set rong_token = null where account_no = '0005';