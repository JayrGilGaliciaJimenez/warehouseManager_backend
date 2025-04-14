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
    WHERE productName = NEW.productName
      AND measurementUnit = NEW.measurementUnit;

    IF existing_quantity IS NOT NULL THEN
        -- If the product exists, update the quantity and totalAmount
    UPDATE stock
    SET quantity     = quantity + NEW.quantity,
        totalAmount = (quantity) * NEW.unitPrice
    WHERE productName = NEW.productName
      AND measurementUnit = NEW.measurementUnit;
    ELSE
        -- If the product does not exist, insert a new record with totalAmount
        INSERT INTO stock (id, productName, measurementUnit, quantity, unitPrice, totalAmount, suplierId)
        VALUES (NEW.id, NEW.productName, NEW.measurementUnit, NEW.quantity, NEW.unitPrice,
                NEW.quantity * NEW.unitPrice, NEW.suplierId);
END IF;
END;

drop trigger product_outs_to_stock;
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
    WHERE productName = NEW.productName
      AND measurementUnit = NEW.measurementUnit;

    IF existing_quantity IS NULL THEN
        -- If the product does not exist, raise an error
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Product does not exist in stock';
    ELSEIF existing_quantity < NEW.quantity THEN
        -- If the product exists but the quantity is insufficient, raise an error
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Insufficient quantity in stock';
    ELSE
        -- If the product exists and the quantity is sufficient, update the quantity and totalAmount
        UPDATE stock
        SET quantity     = quantity - NEW.quantity,
            totalAmount = (quantity) * NEW.unitPrice
        WHERE productName = NEW.productName
          AND measurementUnit = NEW.measurementUnit;
    END IF;
END;
