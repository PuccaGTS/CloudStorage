CREATE TABLE IF NOT EXISTS cloud_storage.files (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    filename varchar(50) NOT NULL,
    size int NOT NULL,
    file_data LONGBLOB NOT NULL,
    user_id bigint NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES cloud_storage.users(id)
    ) ENGINE=InnoDB DEFAULT CHARSET=UTF8;
