package vn.nmn.domusvocationis.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.nmn.domusvocationis.domain.Post;
import vn.nmn.domusvocationis.domain.request.ReqCreateSurveyBulkDTO;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.domain.response.post.ResGetPostByIdDTO;
import vn.nmn.domusvocationis.domain.response.post.ResPostDTO;
import vn.nmn.domusvocationis.service.PostService;
import vn.nmn.domusvocationis.util.annotation.ApiMessage;
import vn.nmn.domusvocationis.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts/{id}")
    @ApiMessage("get a post")
    public ResponseEntity<ResGetPostByIdDTO> getPostById(@PathVariable Long id) throws IdInvalidException {
        Post post = postService.getPostById(id);
        if (post == null) {
            throw new IdInvalidException("Bài viết có id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok(this.postService.convertToResGetPostByIdDTO(post));
    }

    @GetMapping("/posts")
    @ApiMessage("Fetch posts")
    public ResponseEntity<ResPaginationDTO> getListPosts(@Filter Specification<Post> spec, Pageable pageable) {
        return ResponseEntity.ok(this.postService.getListPosts(spec, pageable));
    }

    @PostMapping("/posts")
    @ApiMessage("Create a post")
    public ResponseEntity<ResPostDTO> create(@Valid @RequestBody Post post) throws IdInvalidException {
        if(post.getCategory() == null) throw new IdInvalidException("Category không được để trống");

        Post p = this.postService.create(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.postService.convertToResPostDTO(p));
    }

    @PostMapping("/posts/survey-bulk")
    @ApiMessage("Create a survey post ( with questions and options )")
    public ResponseEntity<ResPostDTO> createBulkSurvey(@Valid @RequestBody ReqCreateSurveyBulkDTO request) {
        Post createdPost = this.postService.createBulkSurvey(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(this.postService.convertToResPostDTO(createdPost));
    }

    @PutMapping("/posts")
    @ApiMessage("Update a post")
    @PreAuthorize("@postService.isOwner(#post.id, authentication.name)")
    public ResponseEntity<ResPostDTO> update(@Valid @RequestBody Post post) throws IdInvalidException {
        Post dbPost = postService.getPostById(post.getId());
        if (dbPost == null) {
            throw new IdInvalidException("Category có id = " + post.getId() + " không tồn tại");
        }

        Post updatedPost = this.postService.update(post);
        return ResponseEntity.status(HttpStatus.OK).body(this.postService.convertToResPostDTO(updatedPost));
    }

    @DeleteMapping("/posts/{id}")
    @ApiMessage("Delete a post")
    @PreAuthorize("@postService.isOwner(#id, authentication.name)")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws IdInvalidException {
        Post post = postService.getPostById(id);
        if (post == null) {
            throw new IdInvalidException("Category có id = " + id + " không tồn tại");
        }

        this.postService.delete(id);
        return ResponseEntity.ok(null);
    }
}
