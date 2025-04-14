package utez.edu.mx.warehousemanager.controller.Category;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utez.edu.mx.warehousemanager.config.ApiResponse;
import utez.edu.mx.warehousemanager.model.Category;
import utez.edu.mx.warehousemanager.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<Category>> save(@RequestBody CategoryDto dto){
        return categoryService.save(dto);
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<Category>>> findAll(){
        return categoryService.findAll();
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Category>> findByUuid(@PathVariable String uuid){
        return categoryService.findByUuid(uuid);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable String  uuid) {
        return categoryService.deleteByUuid(uuid);
    }


}

