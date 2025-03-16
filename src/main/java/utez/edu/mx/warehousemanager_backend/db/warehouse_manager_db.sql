-- Crear la base de datos
DROP DATABASE IF EXISTS warehouse_db;
CREATE DATABASE warehouse_db;
USE warehouse_db;

-- tabla de roles
CREATE TABLE roles
(
    id            INT PRIMARY KEY AUTO_INCREMENT,
    uuid          CHAR(36),
    name          VARCHAR(255) NOT NULL UNIQUE,
    creation_date DATETIME DEFAULT CURRENT_TIMESTAMP
);


-- users table
CREATE TABLE users
(
    id              INT PRIMARY KEY AUTO_INCREMENT,
    uuid            CHAR(36),
    username        VARCHAR(255) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    role_id          INT,
    name            VARCHAR(255),
    lastname        VARCHAR(255),
    email           VARCHAR(255),
    related_user_id INT,
    creation_date   DATETIME DEFAULT CURRENT_TIMESTAMP,
    status         VARCHAR(8) DEFAULT 'Pending',
    FOREIGN KEY (related_user_id) REFERENCES users (id),
    FOREIGN KEY (role_id) REFERENCES roles (id)
);

-- Add related_user_id column to roles table
ALTER TABLE roles
    ADD COLUMN related_user_id INT NULL;

-- Add foreign key constraint to related_user_id column in roles table
ALTER TABLE roles
    ADD FOREIGN KEY (related_user_id) REFERENCES users (id);

-- supliers table
CREATE TABLE supliers
(
    id              INT PRIMARY KEY AUTO_INCREMENT,
    uuid            CHAR(36),
    name            VARCHAR(255) NOT NULL UNIQUE,
    email           VARCHAR(64),
    creation_date   DATETIME DEFAULT CURRENT_TIMESTAMP,
    related_user_id INT     NULL,
    FOREIGN KEY (related_user_id) REFERENCES users (id)
);

-- categories table
CREATE TABLE categories
(
    id              INT PRIMARY KEY AUTO_INCREMENT,
    uuid            CHAR(36),
    name            VARCHAR(255) NOT NULL UNIQUE,
    related_user_id INT     NULL,
    creation_date   DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (related_user_id) REFERENCES users (id)
);

-- product entries table
CREATE TABLE product_entries
(
    id              INT PRIMARY KEY AUTO_INCREMENT,
    uuid            CHAR(36),
    product_name    VARCHAR(64) NOT NULL UNIQUE,
    category_id     INT,
    msurement_unit  VARCHAR(32),
    quantity        INT,
    unit_price      DECIMAL(10, 2) DEFAULT 00.00,
    total_amount    DECIMAL(10, 2) DEFAULT 00.00,
    entry_date      DATETIME       DEFAULT CURRENT_TIMESTAMP,
    suplier_id      INT,
    related_user_id INT    NULL,
    FOREIGN KEY (category_id) REFERENCES categories (id),
    FOREIGN KEY (related_user_id) REFERENCES users (id),
    FOREIGN KEY (suplier_id) REFERENCES supliers (id)
);

-- stock table
CREATE TABLE stock
(
    id             INT PRIMARY KEY AUTO_INCREMENT,
    uuid           CHAR(36),
    product_name   VARCHAR(64),
    msurement_unit VARCHAR(32),
    quantity       INT,
    unit_price     DECIMAL(10, 2) DEFAULT 00.00,
    total_amount   DECIMAL(10, 2) DEFAULT 00.00,
    suplier_id     INT,
    FOREIGN KEY (suplier_id) REFERENCES supliers (id)
);

-- product outs table
CREATE TABLE product_outs
(
    id              INT PRIMARY KEY AUTO_INCREMENT,
    uuid            CHAR(36),
    product_name    VARCHAR(64),
    msurement_unit  VARCHAR(32),
    quantity        INT,
    unit_price      DECIMAL(10, 2) DEFAULT 00.00,
    total_amount    DECIMAL(10, 2) DEFAULT 00.00,
    out_date        DATETIME       DEFAULT CURRENT_TIMESTAMP,
    receiver_name   VARCHAR(32),
    related_user_id INT NULL,
    FOREIGN KEY (related_user_id) REFERENCES users (id)
);

-- transaction log table
CREATE TABLE transaction_log
(
    id               INT PRIMARY KEY AUTO_INCREMENT,
    uuid             CHAR(36),
    transaction_type VARCHAR(50)  NOT NULL,
    table_name       VARCHAR(255) NOT NULL,
    related_user_id  INT,
    details          TEXT,
    transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (related_user_id) REFERENCES users (id)
);

-- TRIGGERS for product_entries, stock and product_outs tables


-- Trigger for INSERT INTO product_entries
CREATE TRIGGER product_entries_to_stock
    AFTER INSERT
    ON product_entries
    FOR EACH ROW
BEGIN
    DECLARE existing_quantity INT;

    -- Check if the product already exists in the stock table
    SELECT quantity
    INTO existing_quantity
    FROM stock
    WHERE product_name = NEW.product_name
      AND msurement_unit = NEW.msurement_unit;

    IF existing_quantity IS NOT NULL THEN
        -- If the product exists, update the quantity and total_amount
    UPDATE stock
    SET quantity     = quantity + NEW.quantity,
        total_amount = (quantity) * NEW.unit_price
    WHERE product_name = NEW.product_name
      AND msurement_unit = NEW.msurement_unit;
    ELSE
        -- If the product does not exist, insert a new record with total_amount
        INSERT INTO stock (id, product_name, msurement_unit, quantity, unit_price, total_amount, suplier_id)
        VALUES (NEW.id, NEW.product_name, NEW.msurement_unit, NEW.quantity, NEW.unit_price,
                NEW.quantity * NEW.unit_price, NEW.suplier_id);
END IF;
END;

-- Trigger for INSERT INTO product outs
CREATE TRIGGER product_outs_to_stock
    AFTER INSERT
    ON product_outs
    FOR EACH ROW
BEGIN
    DECLARE existing_quantity INT;

    -- Check if the product already exists in the stock table
    SELECT quantity
    INTO existing_quantity
    FROM stock
    WHERE product_name = NEW.product_name
      AND msurement_unit = NEW.msurement_unit;

    IF existing_quantity IS NOT NULL THEN
        -- If the product exists, update the quantity and total_amount
    UPDATE stock
    SET quantity     = quantity - NEW.quantity,
        total_amount = (quantity) * NEW.unit_price
    WHERE product_name = NEW.product_name
      AND msurement_unit = NEW.msurement_unit;
    ELSE
        -- If the product does not exist, insert a new record with total_amount
        INSERT INTO stock (id, product_name, msurement_unit, quantity, unit_price, total_amount)
        VALUES (NEW.id, NEW.product_name, NEW.msurement_unit, NEW.quantity, NEW.unit_price,
                NEW.quantity * NEW.unit_price);
END IF;
END;
