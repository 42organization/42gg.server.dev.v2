alter table item
    add column main_content varchar(255);

alter table item
    add column sub_content varchar(255);

UPDATE item SET main_content = content, sub_content = content;

alter table item
drop column content;