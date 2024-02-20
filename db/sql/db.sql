create database db_bank;
\c db_bank;


create table customer(
    id varchar(48) not null,
    first_name varchar(48) not null,
    last_name varchar(48) not null,
    primary key(id)
);
create table account(
    account_id varchar(50) not null,
    customer_id varchar(50) not null,
    balance numeric not null,
    primary key(account_id),
    constraint fk_account_customer_id
        foreign key(customer_id)
        references customer(id)
);
create table transaction(
    transaction_id varchar(50) not null,
    account_id varchar(50) not null,
    amount numeric not null,
    description text,
    transaction_date timestamp not null,
    primary key(transaction_id),
    constraint fk_transaction_account_id
        foreign key(account_id)
        references account(account_id)
);

insert into customer(id, first_name, last_name) values ('JD123456', 'Jane', 'Doe');
insert into account(account_id, customer_id, balance) values ('261389735', 'JD123456', 100.0);

