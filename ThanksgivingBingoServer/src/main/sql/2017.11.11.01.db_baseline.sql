CREATE TABLE users(
    userID SERIAL NOT NULL PRIMARY KEY,
    userName VARCHAR(24) NOT NULL,
    firstName VARCHAR(24),
    lastName VARCHAR(24),
    password VARCHAR NOT NULL,
    tokenSelector VARCHAR,
    tokenValidator VARCHAR,
    isActive boolean not null default 't',
    CONSTRAINT uk_username UNIQUE (userName)
);