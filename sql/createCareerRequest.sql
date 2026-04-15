CREATE TABLE career_request(
    id BIGINT auto_increment primary key,
    body text,
    user_id BIGINT,
    foreign key (user_id) references users(id),
    role_id BIGINT,
    foreign key (role_id) references roles(id),
    is_checked boolean

);