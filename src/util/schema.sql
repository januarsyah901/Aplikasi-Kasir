CREATE
    DATABASE IF NOT EXISTS kasir_db;

SHOW DATABASES;

USE
    kasir_db;

CREATE TABLE IF NOT EXISTS users
(
    id
             INT
        AUTO_INCREMENT
        PRIMARY
            KEY,
    username
             VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role     ENUM
                 (
                     'ADMIN',
                     'CASHIER'
                     )    NOT NULL
);

CREATE TABLE IF NOT EXISTS products
(
    id
               INT
        AUTO_INCREMENT
        PRIMARY
            KEY,
    name
               VARCHAR(100) NOT NULL,
    price      DECIMAL(10,
                   2)       NOT NULL,
    stock      INT          NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transactions
(
    id
                     INT
        AUTO_INCREMENT
        PRIMARY
            KEY,
    cashier_id
                     INT
                            NOT
                                NULL,
    total_amount
                     DECIMAL(10,
                         2) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY
        (
         cashier_id
            ) REFERENCES users
        (
         id
            )
);

CREATE TABLE IF NOT EXISTS transaction_details
(
    id
        INT
        AUTO_INCREMENT
        PRIMARY
            KEY,
    transaction_id
        INT
               NOT
                   NULL,
    product_id
        INT
               NOT
                   NULL,
    quantity
        INT
               NOT
                   NULL,
    price
        DECIMAL(10,
            2) NOT NULL,
    FOREIGN KEY
        (
         transaction_id
            ) REFERENCES transactions
        (
         id
            ),
    FOREIGN KEY
        (
         product_id
            ) REFERENCES products
        (
         id
            )
);

-- Insert default users
INSERT INTO users (username, password, role)
VALUES ('admin', 'admin123', 'ADMIN'),
       ('kasir', 'kasir123', 'CASHIER');

-- Insert some sample products
INSERT INTO products (name, price, stock)
VALUES ('Chocolate Bar', 5000.00, 100),
       ('Milk 1L', 15000.00, 50),
       ('Bread', 10000.00, 30),
       ('Coffee', 20000.00, 45);

-- Query


SHOW TABLES;
