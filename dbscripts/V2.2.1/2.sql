set sql_safe_updates =0;
update qbao_schema.chat_group_member set role = 0 where role is null;