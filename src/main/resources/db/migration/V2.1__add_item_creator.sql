alter table item
    add column creator_intra_id varchar(10) NOT NULL;

alter table item
    add column deleter_intra_id varchar(10);
