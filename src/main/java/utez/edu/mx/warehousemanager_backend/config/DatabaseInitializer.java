package utez.edu.mx.warehousemanager_backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
@Component
public class DatabaseInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    private final JdbcTemplate jdbcTemplate;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {

        String createProductEntriesToStockTrigger = """
    CREATE TRIGGER IF NOT EXISTS product_entries_to_stock
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
                totalAmount = (quantity + NEW.quantity) * NEW.unitPrice
            WHERE productName = NEW.productName
              AND measurementUnit = NEW.measurementUnit;
        ELSE
            -- If the product does not exist, insert a new record with uuid
            INSERT INTO stock (id, productName, measurementUnit, quantity, unitPrice, totalAmount, supplierId, uuid)
            VALUES (NEW.id, NEW.productName, NEW.measurementUnit, NEW.quantity, NEW.unitPrice,
                    NEW.quantity * NEW.unitPrice, NEW.supplierId, UUID());
        END IF;
    END;
    """;

        String createProductOutsToStockTrigger = """
                CREATE TRIGGER IF NOT EXISTS product_outs_to_stock
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
                """;

        String[] transactionLogTriggers = {
                """
                CREATE TRIGGER IF NOT EXISTS after_delete_product_entry
                    AFTER DELETE
                    ON product_entries
                    FOR EACH ROW
                BEGIN
                    DECLARE remaining_quantity INT;
                
                    -- Check the current quantity in the stock table
                    SELECT quantity
                    INTO remaining_quantity
                    FROM stock
                    WHERE productName = OLD.productName
                      AND measurementUnit = OLD.measurementUnit;
                
                    IF remaining_quantity IS NOT NULL THEN
                        -- Subtract the deleted product entry's quantity from the stock
                        UPDATE stock
                        SET quantity = quantity - OLD.quantity,
                            totalAmount = (quantity - OLD.quantity) * unitPrice
                        WHERE productName = OLD.productName
                          AND measurementUnit = OLD.measurementUnit;
                
                        -- If the resulting quantity is zero or less, delete the stock entry
                        DELETE FROM stock
                        WHERE productName = OLD.productName
                          AND measurementUnit = OLD.measurementUnit
                          AND quantity <= 0;
                    END IF;
                END;
                """,
                """
                CREATE TRIGGER IF NOT EXISTS after_suppliers_insert
                    AFTER INSERT
                    ON suppliers
                    FOR EACH ROW
                BEGIN
                    DECLARE var_relatedUserId INT;
                    SELECT relatedUserId INTO var_relatedUserId FROM suppliers ORDER BY id DESC LIMIT 1;
                
                    INSERT INTO transaction_log (transactionType, tableName, relatedUserId, details, uuid)
                    VALUES ('INSERT', 'suppliers', var_relatedUserId, CONCAT('Inserted supplier with id: ', NEW.id), UUID());
                
                END;
                """,
                """
                CREATE TRIGGER IF NOT EXISTS after_suppliers_update
                    AFTER UPDATE
                    ON suppliers
                    FOR EACH ROW
                BEGIN
                    DECLARE var_relatedUserId INT;
                    SELECT relatedUserId INTO var_relatedUserId FROM suppliers ORDER BY id DESC LIMIT 1;
                
                    INSERT INTO transaction_log (transactionType, tableName, relatedUserId, details, uuid)
                    VALUES ('UPDATE', 'suppliers', var_relatedUserId, CONCAT('Updated supplier with id: ', NEW.id), UUID());
                
                END;
                """,
                """
                CREATE TRIGGER IF NOT EXISTS after_suppliers_delete
                    AFTER DELETE
                    ON suppliers
                    FOR EACH ROW
                BEGIN
                    DECLARE var_relatedUserId INT;
                    SELECT relatedUserId INTO var_relatedUserId FROM suppliers ORDER BY id DESC LIMIT 1;
                
                    INSERT INTO transaction_log (transactionType, tableName, relatedUserId, details, uuid)
                    VALUES ('DELETE', 'suppliers', var_relatedUserId, CONCAT('Deleted supplier with name: ', OLD.name), UUID());
                
                END;
                """,
                """
                CREATE TRIGGER IF NOT EXISTS after_categories_insert
                    AFTER INSERT
                    ON categories
                    FOR EACH ROW
                BEGIN
                    DECLARE var_relatedUserId INT;
                    SELECT relatedUserId INTO var_relatedUserId FROM categories ORDER BY id DESC LIMIT 1;
                
                    INSERT INTO transaction_log (transactionType, tableName, relatedUserId, details, uuid)
                    VALUES ('INSERT', 'categories', var_relatedUserId, CONCAT('Inserted category with id: ', NEW.id), UUID());
                
                END;
                """,

                """
                CREATE TRIGGER IF NOT EXISTS after_categories_update
                    AFTER UPDATE
                    ON categories
                    FOR EACH ROW
                BEGIN
                    DECLARE var_relatedUserId INT;
                
                    SELECT relatedUserId INTO var_relatedUserId FROM categories ORDER BY id DESC LIMIT 1;
                
                    INSERT INTO transaction_log (transactionType, tableName, relatedUserId, details, uuid)
                    VALUES ('UPDATE', 'categories', var_relatedUserId, CONCAT('Updated category with id: ', NEW.id), UUID());
                
                END;
                """,
                """
                CREATE TRIGGER IF NOT EXISTS after_categories_delete
                    AFTER DELETE
                    ON categories
                    FOR EACH ROW
                BEGIN
                    DECLARE var_relatedUserId INT;
                    SELECT relatedUserId INTO var_relatedUserId FROM categories ORDER BY id DESC LIMIT 1;
                
                    INSERT INTO transaction_log (transactionType, tableName, relatedUserId, details, uuid)
                    VALUES ('DELETE', 'categories', var_relatedUserId, CONCAT('Deleted category with name: ', OLD.name), UUID());
                
                END;
                """,
                """
               CREATE TRIGGER IF NOT EXISTS after_product_entries_insert
                        AFTER INSERT
                        ON product_entries
                        FOR EACH ROW
                    BEGIN
                        DECLARE var_relatedUserUUID UUID;
                        SELECT relatedUserUUID INTO var_relatedUserUUID FROM product_entries ORDER BY id DESC LIMIT 1;
                    
                        INSERT INTO transaction_log (transactionType, tableName, relatedUserUUID, details, uuid)
                        VALUES ('INSERT', 'product_entries', var_relatedUserUUID, CONCAT('Inserted product entry with id: ', NEW.id), UUID());
                    END;
                    
                """,
                """
                CREATE TRIGGER IF NOT EXISTS after_product_entries_update
                    AFTER UPDATE
                    ON product_entries
                    FOR EACH ROW
                BEGIN
                    DECLARE var_relatedUserId INT;
                    SELECT relatedUserId INTO var_relatedUserId FROM product_entries ORDER BY id DESC LIMIT 1;
                
                    INSERT INTO transaction_log (transactionType, tableName, relatedUserId, details, uuid)
                    VALUES ('UPDATE', 'product_entries', var_relatedUserId, CONCAT('Updated product entry with id: ', NEW.id), UUID());
                END;
                """,
                """
                CREATE TRIGGER IF NOT EXISTS after_product_entries_delete
                    AFTER DELETE
                    ON product_entries
                    FOR EACH ROW
                BEGIN
                    DECLARE var_relatedUserId INT;
                    SELECT relatedUserId INTO var_relatedUserId FROM product_entries ORDER BY id DESC LIMIT 1;
                
                    INSERT INTO transaction_log (transactionType, tableName, relatedUserId, details, uuid)
                    VALUES ('DELETE', 'product_entries', var_relatedUserId,
                            CONCAT('Deleted product entry with name: ', OLD.productName), UUID());
                END;
                """,
                """
                CREATE TRIGGER IF NOT EXISTS after_product_outs_insert
                    AFTER INSERT
                    ON product_outs
                    FOR EACH ROW
                BEGIN
                    DECLARE var_relatedUserId INT;
                    SELECT relatedUserId INTO var_relatedUserId FROM product_outs ORDER BY id DESC LIMIT 1;
                
                    INSERT INTO transaction_log (transactionType, tableName, relatedUserId, details, uuid)
                    VALUES ('INSERT', 'product_outs', var_relatedUserId, CONCAT('Inserted product out with id: ', NEW.id), UUID());
                END;
                """,
                """
                CREATE TRIGGER IF NOT EXISTS after_product_outs_insert
                    AFTER INSERT
                    ON product_outs
                    FOR EACH ROW
                BEGIN
                    DECLARE var_relatedUserId INT;
                    SELECT relatedUserId INTO var_relatedUserId FROM product_outs ORDER BY id DESC LIMIT 1;
                
                    INSERT INTO transaction_log (transactionType, tableName, relatedUserId, details, uuid)
                    VALUES ('INSERT', 'product_outs', var_relatedUserId, CONCAT('Inserted product out with id: ', NEW.id), UUID());
                END;
                """,
                """
                CREATE TRIGGER IF NOT EXISTS after_product_outs_update
                    AFTER UPDATE
                    ON product_outs
                    FOR EACH ROW
                BEGIN
                    DECLARE var_relatedUserId INT;
                    SELECT relatedUserId INTO var_relatedUserId FROM product_outs ORDER BY id DESC LIMIT 1;
                
                    INSERT INTO transaction_log (transactionType, tableName, relatedUserId, details, uuid)
                    VALUES ('UPDATE', 'product_outs', var_relatedUserId, CONCAT('Updated product out with id: ', NEW.id), UUID());
                END;
                """,
                """
                CREATE TRIGGER IF NOT EXISTS after_product_outs_update
                    AFTER UPDATE
                    ON product_outs
                    FOR EACH ROW
                BEGIN
                    DECLARE var_relatedUserId INT;
                    SELECT relatedUserId INTO var_relatedUserId FROM product_outs ORDER BY id DESC LIMIT 1;
                
                    INSERT INTO transaction_log (transactionType, tableName, relatedUserId, details, uuid)
                    VALUES ('UPDATE', 'product_outs', var_relatedUserId, CONCAT('Updated product out with id: ', NEW.id), UUID());
                END;
                """,
                """
                CREATE TRIGGER IF NOT EXISTS after_product_outs_delete
                    AFTER DELETE
                    ON product_outs
                    FOR EACH ROW
                BEGIN
                    DECLARE var_relatedUserId INT;
                    SELECT relatedUserId INTO var_relatedUserId FROM product_outs ORDER BY id DESC LIMIT 1;
                
                    INSERT INTO transaction_log (transactionType, tableName, relatedUserId, details, uuid)
                    VALUES ('DELETE', 'product_outs', var_relatedUserId, CONCAT('Deleted product out with name: ', OLD.productName), UUID());
                END;
                """
        };

        try {
            jdbcTemplate.execute(createProductEntriesToStockTrigger);
            logger.info("Trigger 'product_entries_to_stock' created successfully.");
        } catch (Exception e) {
            logger.error("Error creating trigger 'product_entries_to_stock': ", e);
        }

        try {
            jdbcTemplate.execute(createProductOutsToStockTrigger);
            logger.info("Trigger 'product_outs_to_stock' created successfully.");
        } catch (Exception e) {
            logger.error("Error creating trigger 'product_outs_to_stock': ", e);
        }

        int counter = 1;
        for (String createTransactionLogTrigger : transactionLogTriggers) {
            try {
                jdbcTemplate.execute(createTransactionLogTrigger);
                logger.info("{}/{} Transaction log trigger created successfully", counter, transactionLogTriggers.length);
            } catch (Exception e) {
                logger.error("Error creating transaction log triggers: ", e);
            }
            counter++;
        }


    }
}
