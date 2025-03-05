package utez.edu.mx.warehousemanager_backend.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Utilities {

    private Utilities() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static ResponseEntity<Object> generateResponse(HttpStatus status, String mensaje) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("fecha", new Date());
            map.put("status", status.value());
            map.put("mensaje", mensaje);
            return new ResponseEntity<>(map, status);
        } catch (Exception e) {
            map.clear();
            map.put("fecha", new Date());
            map.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            map.put("mensaje", e.getMessage());
            return new ResponseEntity<>(map, status);
        }
    }
}
