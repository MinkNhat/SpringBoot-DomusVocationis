package vn.nmn.domusvocationis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.nmn.domusvocationis.domain.Category;
import vn.nmn.domusvocationis.domain.Post;

@Repository
@Transactional
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    void deleteByCategory(Category category);
}
