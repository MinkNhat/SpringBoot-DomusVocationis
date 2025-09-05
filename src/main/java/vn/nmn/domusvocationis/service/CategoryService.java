package vn.nmn.domusvocationis.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.nmn.domusvocationis.domain.Category;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.repository.CategoryRepository;
import vn.nmn.domusvocationis.repository.PostRepository;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    public CategoryService(CategoryRepository categoryRepository, PostRepository postRepository) {
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
    }

    public Category getCategoryById(Long id) {
        return this.categoryRepository.findById(id).orElse(null);
    }

    public ResPaginationDTO getListCategories(Specification<Category> spec, Pageable pageable) {
        Page<Category> catePage = this.categoryRepository.findAll(spec, pageable);
        ResPaginationDTO rs = new ResPaginationDTO();
        ResPaginationDTO.Meta mt = new ResPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(catePage.getTotalPages());
        mt.setTotal(catePage.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(catePage.getContent());

        return rs;
    }

    public Category create(Category cate) {
        return this.categoryRepository.save(cate);
    }

    public Category update(Category cate, Category dbCategory) {
        if(dbCategory != null) {
            dbCategory.setName(cate.getName());
            dbCategory.setDescription(cate.getDescription());
            dbCategory.setActive(cate.isActive());
            dbCategory.setAllowPost(cate.isAllowPost());
            return this.categoryRepository.save(dbCategory);
        }

        return null;
    }

    public void delete(Long id) {
        Category cate = this.getCategoryById(id);
        this.postRepository.deleteByCategory(cate);
        this.categoryRepository.deleteById(id);
    }
}
