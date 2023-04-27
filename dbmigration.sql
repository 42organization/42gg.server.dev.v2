use MAIN;
alter table user modify column role_type VARCHAR(20);
update user set role_type = 'USER' where role_type = '1';
update user set role_type = 'ADMIN' where role_type = '2';

alter table user modify column racket_type VARCHAR(10);
update user set racket_type = 'PENHOLDER' where racket_type='0';
update user set racket_type = 'SHAKEHAND' where racket_type='1';
update user set racket_type = 'DUAL' where racket_type='2';
update user set racket_type = 'NONE' where racket_type='3';
