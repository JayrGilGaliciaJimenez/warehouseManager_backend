package utez.edu.mx.warehousemanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class WarehouseManagerBackendApplicationTests {

    @Test
    void contextLoads() {
        // Verify that the application context loads without errors
        assertDoesNotThrow(() -> WarehouseManagerBackendApplication.main(new String[]{}));
    }

}
