set sql_safe_updates = 0;

insert into exchange_log (address, amount, exchange_time, transaction_hash, unit,type, status,account_no,from_address,fee,create_time,update_time)
values ('QNaYh7KqLHkVxMrNtMTMoR7BNypizUXNJu', 200, '2017-12-24 16:42:24', '308890cef3076001fe9aa60554895c7e1e18610a94d81f62fde2409fa58faf5d', '6','1','1','300559','QNFv8EoyX8cBgrKBZL6P991CBarVjfpa7j','0.0946','2017-12-24 16:42:24','2017-12-24 16:42:24');
UPDATE account_balance
SET
    amount = amount + 200
WHERE
    account_no = '300559' AND unit = 6;

insert into exchange_log (address, amount, exchange_time, transaction_hash, unit,type, status,account_no,from_address,fee,create_time,update_time)
values ('QNaYh7KqLHkVxMrNtMTMoR7BNypizUXNJu', 100, '2017-12-24 20:13:52', 'faa86199e0f3d877df0c14df98d72a2b7f916b6ee7565ad0b3cc1bd0e8c3d047', '6','1','1','300559','QNFv8EoyX8cBgrKBZL6P991CBarVjfpa7j','0.0946','2017-12-24 20:13:52','2017-12-24 20:13:52');
UPDATE account_balance
SET
    amount = amount + 100
WHERE
    account_no = '300559' AND unit = 6;

insert into exchange_log (address, amount, exchange_time, transaction_hash, unit,type, status,account_no,from_address,fee,create_time,update_time)
values ('QNaYh7KqLHkVxMrNtMTMoR7BNypizUXNJu', 100, '2017-12-24 20:30:40', '6c79733fc53fe76287dfabd685bc711c6a84279ed8997f6eefee628b38006a56', '6','1','1','300559','QNFv8EoyX8cBgrKBZL6P991CBarVjfpa7j','0.0946','2017-12-24 20:30:40','2017-12-24 20:30:40');
UPDATE account_balance
SET
    amount = amount + 100
WHERE
    account_no = '300559' AND unit = 6;

insert into exchange_log (address, amount, exchange_time, transaction_hash, unit,type, status,account_no,from_address,fee,create_time,update_time)
values ('QNaYh7KqLHkVxMrNtMTMoR7BNypizUXNJu', 100, '2017-12-24 20:32:00', 'd0a20835a958cd16114fcba0ebc030d9cc76811f089e4f77e758015b035d3d8d', '6','1','1','300559','QNFv8EoyX8cBgrKBZL6P991CBarVjfpa7j','0.0946','2017-12-24 20:32:00','2017-12-24 20:32:00');
UPDATE account_balance
SET
    amount = amount + 100
WHERE
    account_no = '300559' AND unit = 6;

insert into exchange_log (address, amount, exchange_time, transaction_hash, unit,type, status,account_no,from_address,fee,create_time,update_time)
values ('QNaYh7KqLHkVxMrNtMTMoR7BNypizUXNJu', 1000, '2017-12-22 09:02:08', '2dac41ed96f0991d95f3d67e1f2d047c42cfb60f8c28c25e7a8f38d76a5c27ed', '6','1','1','918703','QQoU895WJ1NfCzHp6dEN8HEi9b8iuRvzQy','0.0946','2017-12-22 09:02:08','2017-12-22 09:02:08');
UPDATE account_balance
SET
    amount = amount + 1000
WHERE
    account_no = '918703' AND unit = 6;

insert into exchange_log (address, amount, exchange_time, transaction_hash, unit,type, status,account_no,from_address,fee,create_time,update_time)
values ('QNaYh7KqLHkVxMrNtMTMoR7BNypizUXNJu', 1000, '2017-12-22 09:03:12', '4fdfc7e7ac82fb20c2d739d012892baa5c33ab303f023aef527c4a1fdb3ce71c', '6','1','1','918703','QQoU895WJ1NfCzHp6dEN8HEi9b8iuRvzQy','0.0946','2017-12-22 09:03:12','2017-12-22 09:03:12');
UPDATE account_balance
SET
    amount = amount + 1000
WHERE
    account_no = '918703' AND unit = 6;

insert into exchange_log (address, amount, exchange_time, transaction_hash, unit,type, status,account_no,from_address,fee,create_time,update_time)
values ('QNaYh7KqLHkVxMrNtMTMoR7BNypizUXNJu', 1000, '2017-12-22 09:03:12', '6efe1766db38bc839f9005e1e31caac43ee6201aeb40d9d5ec95b474947ddb65', '6','1','1','918703','QQoU895WJ1NfCzHp6dEN8HEi9b8iuRvzQy','0.0946','2017-12-22 09:03:12','2017-12-22 09:03:12');
UPDATE account_balance
SET
    amount = amount + 1000
WHERE
    account_no = '918703' AND unit = 6;

insert into exchange_log (address, amount, exchange_time, transaction_hash, unit,type, status,account_no,from_address,fee,create_time,update_time)
values ('QNaYh7KqLHkVxMrNtMTMoR7BNypizUXNJu', 75, '2017-12-22 09:21:04', '1a976aa5502a3595a0be48ffc88c7ff0beb6f91609b1a1c46a672aafb7bb4c63', '6','1','1','918703','QQoU895WJ1NfCzHp6dEN8HEi9b8iuRvzQy','0.0946','2017-12-22 09:21:04','2017-12-22 09:21:04');
UPDATE account_balance
SET
    amount = amount + 75
WHERE
    account_no = '918703' AND unit = 6;

insert into exchange_log (address, amount, exchange_time, transaction_hash, unit,type, status,account_no,from_address,fee,create_time,update_time)
values ('QNaYh7KqLHkVxMrNtMTMoR7BNypizUXNJu', 75, '2017-12-22 09:21:04', '12f0b550899a57574fdb1a568e4a8a8c336a77826afca57f434991467b2e654b', '6','1','1','918703','QQoU895WJ1NfCzHp6dEN8HEi9b8iuRvzQy','0.0946','2017-12-22 09:21:04','2017-12-22 09:21:04');
UPDATE account_balance
SET
    amount = amount + 75
WHERE
    account_no = '918703' AND unit = 6;

insert into exchange_log (address, amount, exchange_time, transaction_hash, unit,type, status,account_no,from_address,fee,create_time,update_time)
values ('QNaYh7KqLHkVxMrNtMTMoR7BNypizUXNJu', 75, '2017-12-22 09:21:04', 'bbd2c25b5c37c27f5a4d66ba60a64d9531bfec5b83323ca37e56f9a382e4b30a', '6','1','1','918703','QQoU895WJ1NfCzHp6dEN8HEi9b8iuRvzQy','0.0946','2017-12-22 09:21:04','2017-12-22 09:21:04');
UPDATE account_balance
SET
    amount = amount + 75
WHERE
    account_no = '918703' AND unit = 6;


insert into exchange_log (address, amount, exchange_time, transaction_hash, unit, type, status, account_no, from_address, fee, create_time, update_time)
values ('QNaYh7KqLHkVxMrNtMTMoR7BNypizUXNJu', 1, '2017-12-24 22:39:44', '2685a267c4dd2961283bcb4d7e84b346ddf9a49260176bcfd372716f57cd3081', '6','1','1','423612','QLsfoiiBTB825YaaZV8tnu8TvFabUwohKv','0.086','2017-12-24 22:39:44','2017-12-24 22:39:44');
UPDATE account_balance
SET
    amount = amount + 1
WHERE
    account_no = '423612' AND unit = 6;


insert into exchange_log (address, amount, exchange_time, transaction_hash, unit, type, status, account_no, from_address, fee, create_time, update_time)
values ('QNaYh7KqLHkVxMrNtMTMoR7BNypizUXNJu', 0.003, '2017-12-25 00:46:08', 'cba0cbfa8703c026fa2d61d77242ea1ee2a4609ec52d6cebe9fa3c9da20a4e4a', '6','1','1','139127','QgSAeety341xKDDMwEHWbfLZRuE2ZzBk8G','0.086','2017-12-25 00:46:08','2017-12-25 00:46:08');
UPDATE account_balance
SET
    amount = amount + 0.003
WHERE
    account_no = '139127' AND unit = 6;

insert into exchange_log (address, amount, exchange_time, transaction_hash, unit, type, status, account_no, from_address, fee, create_time, update_time)
values ('QNaYh7KqLHkVxMrNtMTMoR7BNypizUXNJu', 0.003, '2017-12-25 00:46:08', '511086edb243d554ec5a45342013fe567076dfe6eb21108da0b64ba3e4a0cfc3', '6','1','1','139127','QgSAeety341xKDDMwEHWbfLZRuE2ZzBk8G','0.086','2017-12-25 00:46:08','2017-12-25 00:46:08');
UPDATE account_balance
SET
    amount = amount + 0.003
WHERE
    account_no = '139127' AND unit = 6;

insert into exchange_log (address, amount, exchange_time, transaction_hash, unit, type, status, account_no, from_address, fee, create_time, update_time)
values ('QNaYh7KqLHkVxMrNtMTMoR7BNypizUXNJu', 2, '2017-12-26 19:33:36', 'cf13e80c58a3e18ddc3195742a8a75a50d1bac94265593bb377781838c6e14b5', '6','1','1','445899','QiDuAEAyPqiZgJSodwQAJXsZ4B5ytRYLAP','0.0946','2017-12-26 19:33:36','2017-12-26 19:33:36');
UPDATE account_balance
SET
    amount = amount + 2
WHERE
    account_no = '445899' AND unit = 6;

insert into exchange_log (address, amount, exchange_time, transaction_hash, unit, type, status, account_no, from_address, fee, create_time, update_time)
values ('QNaYh7KqLHkVxMrNtMTMoR7BNypizUXNJu', 2, '2017-12-26 22:41:04', 'f49e83b3a5dd8c8da78e7e860d4aeedb32609de67c7c70c1d2e47de602aa84e2', '6','1','1','445899','QiDuAEAyPqiZgJSodwQAJXsZ4B5ytRYLAP','0.0946','2017-12-26 22:41:04','2017-12-26 22:41:04');
UPDATE account_balance
SET
    amount = amount + 2
WHERE
    account_no = '445899' AND unit = 6;