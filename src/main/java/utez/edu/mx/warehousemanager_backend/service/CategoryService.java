package utez.edu.mx.warehousemanager_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import utez.edu.mx.warehousemanager_backend.config.ApiResponse;
import utez.edu.mx.warehousemanager_backend.controller.Category.CategoryDto;
import utez.edu.mx.warehousemanager_backend.model.Category;
import utez.edu.mx.warehousemanager_backend.repository.CategoryRepository;

import java.time.LocalDateTime;
import java.util.List;



@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;

    }

    public ResponseEntity<ApiResponse<Category>> save(CategoryDto dto) {
        int existingCategoriesCount = categoryRepository.countCoincidencesByName(dto.getName().trim());
        if (existingCategoriesCount > 0) {
            ApiResponse<Category> response = new ApiResponse<>(
                    "Category already exists",
                    HttpStatus.CONFLICT
            );
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        Category savedCategory = Category.builder()
                .name(dto.getName())
                .creationDate(LocalDateTime.now())
                .build();

        ApiResponse<Category> response = new ApiResponse<>(
                categoryRepository.save(savedCategory),
                "New category created",
                HttpStatus.OK
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<List<Category>>> findAll (){
        ApiResponse<List<Category>> response = new ApiResponse<>(
                categoryRepository.findAll(),
                "All categories list",
                HttpStatus.OK
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<Category>> findByUuid(String uuid){
        if(categoryRepository.findByUuid(uuid) != null){
            ApiResponse<Category> response = new ApiResponse<>(
                    categoryRepository.findByUuid(uuid),
                    "Category found",
                    HttpStatus.OK
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<Category> response = new ApiResponse<>(
                    "Category not found",
                    HttpStatus.NOT_FOUND
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ApiResponse<Void>> deleteByUuid(String  uuid) {
        Category category = categoryRepository.findByUuid(uuid);
        if(category != null) {
            categoryRepository.deleteById(category.getId());
            ApiResponse<Void> response = new ApiResponse<>(
                    "Category deleted",
                    HttpStatus.OK
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<Void> response = new ApiResponse<>(
                    "Category not found",
                    HttpStatus.NOT_FOUND
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

}
