package vn.nmn.domusvocationis.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.nmn.domusvocationis.domain.Category;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.service.CategoryService;
import vn.nmn.domusvocationis.util.annotation.ApiMessage;
import vn.nmn.domusvocationis.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories/{id}")
    @ApiMessage("get a category")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) throws IdInvalidException {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            throw new IdInvalidException("Category có id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok(category);
    }

    @GetMapping("/categories")
    @ApiMessage("Fetch categories")
    public ResponseEntity<ResPaginationDTO> getListCategories(@Filter Specification<Category> spec, Pageable pageable) {
        return ResponseEntity.ok(this.categoryService.getListCategories(spec, pageable));
    }

    @PostMapping("/categories")
    @ApiMessage("Create a category")
    public ResponseEntity<Category> create(@Valid @RequestBody Category category) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.categoryService.create(category));
    }

    @PutMapping("/categories")
    @ApiMessage("Update a category")
    public ResponseEntity<Category> update(@Valid @RequestBody Category category) throws IdInvalidException {
        Category dbCategory = categoryService.getCategoryById(category.getId());
        if (dbCategory == null) {
            throw new IdInvalidException("Category có id = " + category.getId() + " không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.categoryService.update(category, dbCategory));
    }

    @DeleteMapping("/categories/{id}")
    @ApiMessage("Delete a category")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws IdInvalidException {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            throw new IdInvalidException("Category có id = " + id + " không tồn tại");
        }

        this.categoryService.delete(id);
        return ResponseEntity.ok(null);
    }
}
