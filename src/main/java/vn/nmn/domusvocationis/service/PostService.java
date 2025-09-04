package vn.nmn.domusvocationis.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.nmn.domusvocationis.domain.Category;
import vn.nmn.domusvocationis.domain.Post;
import vn.nmn.domusvocationis.domain.User;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.domain.response.post.ResPostDTO;
import vn.nmn.domusvocationis.domain.response.user.ResUserDTO;
import vn.nmn.domusvocationis.repository.PostRepository;
import vn.nmn.domusvocationis.repository.UserRepository;
import vn.nmn.domusvocationis.util.SecurityUtil;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;

    public PostService(PostRepository postRepository, UserRepository userRepository, CategoryService categoryService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryService = categoryService;
    }

    public boolean isOwner(Long postId, String email) {
        Post post = postRepository.findById(postId).orElse(null);
        return post != null && post.getCreatedBy().equals(email);
    }

    public ResPostDTO convertToResPostDTO(Post post) {
        ResPostDTO res = new ResPostDTO();

        res.setId(post.getId());
        res.setTitle(post.getTitle());
        res.setContent(post.getContent());
        res.setType(post.getType());
        res.setStatus(post.getStatus());
        res.setPublicPost(post.getPublicPost());
        res.setExpiresAt(post.getExpiresAt());

        res.setCreatedAt(post.getCreatedAt());
        res.setUpdatedAt(post.getUpdatedAt());
        res.setCreatedBy(post.getCreatedBy());

        if(post.getUser() != null) {
            ResPostDTO.UserPost user = new ResPostDTO.UserPost(post.getUser().getId(), post.getUser().getFullName(), post.getUser().getAvatar());
            res.setUser(user);
        }

        if(post.getCategory() != null) {
            res.setCategory(new ResPostDTO.CategoryPost(post.getCategory().getId(), post.getCategory().getName()));
        }

        return res;
    }

    public Post getPostById(Long id) {
        return this.postRepository.findById(id).orElse(null);
    }

    public ResPaginationDTO getListPosts(Specification<Post> spec, Pageable pageable) {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User currentUser = this.userRepository.findByEmail(email);

        if(currentUser == null) {
            Specification<Post> publicOnly = (root, query, cb) -> cb.isTrue(root.get("publicPost"));
            spec = spec.and(publicOnly);
        }

        Page<Post> postPage = this.postRepository.findAll(spec, pageable);
        ResPaginationDTO rs = new ResPaginationDTO();
        ResPaginationDTO.Meta mt = new ResPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(postPage.getTotalPages());
        mt.setTotal(postPage.getTotalElements());

        List<ResPostDTO> listPosts = postPage.getContent().stream().map(item -> this.convertToResPostDTO(item)).toList();

        rs.setMeta(mt);
        rs.setResult(listPosts);

        return rs;
    }

    public Post create(Post p) {
        if(p.getCategory() != null) {
            Category cate = this.categoryService.getCategoryById(p.getCategory().getId());
            if(cate != null)
                p.setCategory(cate);
            else throw new IllegalArgumentException("Category có id = " + p.getCategory().getId() + " không tồn tại");
        }

        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User currentUser = this.userRepository.findByEmail(email);
        p.setUser(currentUser);

        return this.postRepository.save(p);
    }

    public Post update(Post r) {
        Post dbPost = this.getPostById(r.getId());
        if(dbPost != null) {
            dbPost.setTitle(r.getTitle());
            dbPost.setContent(r.getContent());

            return this.postRepository.save(dbPost);
        }

        return null;
    }

    public void delete(Long id) {
        this.postRepository.deleteById(id);
    }
}
