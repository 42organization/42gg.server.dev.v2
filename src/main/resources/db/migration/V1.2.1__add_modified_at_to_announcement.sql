alter table announcement change column created_time created_at DATETIME not null;
alter table announcement add column modified_at DATETIME;
update announcement set modified_at=deleted_at where modified_at is null;