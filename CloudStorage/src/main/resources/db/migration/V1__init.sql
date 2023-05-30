CREATE TABLE IF NOT EXISTS cloud_storage.users (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    email varchar(50) NOT NULL,
    password varchar(255) NOT NULL,
    role varchar(255),
    PRIMARY KEY (id),
    CONSTRAINT uq_email UNIQUE (email)
    ) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

INSERT INTO users(id, email, password, role)
values (1,
        'admin@test.com',
        '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a',
        'USER'),
       (2,
        'user@test.com',
        '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a',
        'USER');
