alter table megaphone
    add column receipt_id bigint;

alter table megaphone
    add constraint fk_megaphone_receipt_receipt_id
        foreign key (receipt_id)
            references receipt (id);