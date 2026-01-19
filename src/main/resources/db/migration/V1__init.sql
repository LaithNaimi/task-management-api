-- Categories
create table if not exists category (
                                        id bigserial primary key,
                                        name varchar(50) not null unique,
                                        created_at timestamp not null
);

-- Tasks
create table if not exists task (
                                    id bigserial primary key,
                                    title varchar(255) not null,
                                    description text,
                                    status varchar(30) not null,
                                    priority varchar(30) not null,
                                    category_id bigint,
                                    due_date date,
                                    created_at timestamp not null,
                                    updated_at timestamp not null,
                                    constraint fk_task_category foreign key (category_id) references category(id)
);

create index if not exists idx_task_category_id on task(category_id);
create index if not exists idx_task_status on task(status);
create index if not exists idx_task_priority on task(priority);
