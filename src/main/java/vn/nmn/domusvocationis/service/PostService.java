package vn.nmn.domusvocationis.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nmn.domusvocationis.domain.*;
import vn.nmn.domusvocationis.domain.request.ReqCreateSurveyBulkDTO;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.domain.response.post.ResGetPostByIdDTO;
import vn.nmn.domusvocationis.domain.response.post.ResPostDTO;
import vn.nmn.domusvocationis.domain.response.user.ResUserDTO;
import vn.nmn.domusvocationis.repository.*;
import vn.nmn.domusvocationis.util.SecurityUtil;
import vn.nmn.domusvocationis.util.constant.QuestionTypeEnum;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final CategoryService categoryService;
    private final AnswerRepository answerRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, QuestionRepository questionRepository, OptionRepository optionRepository, CategoryService categoryService, AnswerRepository answerRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.categoryService = categoryService;
        this.answerRepository = answerRepository;
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

    public ResGetPostByIdDTO convertToResGetPostByIdDTO(Post post) {
        ResGetPostByIdDTO res = new ResGetPostByIdDTO();

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

        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User currentUser = this.userRepository.findByEmail(email);
        res.setSubmitted(answerRepository.existsByQuestion_Post_IdAndUser_Id(post.getId(), currentUser.getId()));


        if(post.getUser() != null) {
            ResGetPostByIdDTO.UserPost user = new ResGetPostByIdDTO.UserPost(post.getUser().getId(), post.getUser().getFullName(), post.getUser().getAvatar());
            res.setUser(user);
        }

        if(post.getCategory() != null) {
            res.setCategory(new ResGetPostByIdDTO.CategoryPost(post.getCategory().getId(), post.getCategory().getName()));
        }

        // questions + options
        if (post.getQuestions() != null && !post.getQuestions().isEmpty()) {
            List<ResGetPostByIdDTO.QuestionPost> questionDtos = post.getQuestions()
                    .stream()
                    .map(q -> {
                        ResGetPostByIdDTO.QuestionPost dto = new ResGetPostByIdDTO.QuestionPost();
                        dto.setId(q.getId());
                        dto.setQuestionText(q.getQuestionText());
                        dto.setType(q.getType());
                        dto.setOrderDisplay(q.getOrderDisplay());
                        dto.setRequired(q.getRequired());
                        dto.setAllowMultiple(q.getAllowMultiple());

                        if (q.getOptions() != null && !q.getOptions().isEmpty()) {
                            List<ResGetPostByIdDTO.OptionQuestion> optionsDto = q.getOptions()
                                    .stream()
                                    .map(o -> {
                                        ResGetPostByIdDTO.OptionQuestion optionDto = new ResGetPostByIdDTO.OptionQuestion();
                                        optionDto.setId(o.getId());
                                        optionDto.setOptionText(o.getOptionText());
                                        optionDto.setOrderDisplay(o.getOrderDisplay());
                                        return optionDto;
                                    })
                                    .toList();
                            dto.setOptions(optionsDto);
                        }
                        return dto;
                    })
                    .toList();
            res.setQuestions(questionDtos);
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

    @Transactional
    public Post createBulkSurvey(ReqCreateSurveyBulkDTO request) {
        Category category = this.categoryService.getCategoryById(request.getCategoryId());
        if (category == null) {
            throw new IllegalArgumentException("Category có id = " + request.getCategoryId() + " không tồn tại");
        }

        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User currentUser = this.userRepository.findByEmail(email);

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setType(request.getType());
        post.setStatus(request.getStatus());
        post.setExpiresAt(request.getExpiresAt());
        post.setPublicPost(request.getPublicPost());
        post.setCategory(category);
        post.setUser(currentUser);

        // Save post to get ID
        Post savedPost = this.postRepository.save(post);

        List<Question> questions = request.getQuestions().stream().map(questionDTO -> {
            Question question = new Question();
            question.setQuestionText(questionDTO.getQuestionText());
            question.setType(questionDTO.getType());
            question.setOrderDisplay(questionDTO.getOrderDisplay());
            question.setRequired(questionDTO.getRequired());
            question.setAllowMultiple(questionDTO.getAllowMultiple());
            question.setPost(savedPost);

            Question savedQuestion = this.questionRepository.save(question);

            // Create options if MULTIPLE_CHOICE
            if (questionDTO.getType() == QuestionTypeEnum.MULTIPLE_CHOICE &&
                    questionDTO.getOptions() != null && !questionDTO.getOptions().isEmpty()) {

                List<Option> options = questionDTO.getOptions().stream().map(optionDTO -> {
                    Option option = new Option();
                    option.setOptionText(optionDTO.getOptionText());
                    option.setOrderDisplay(optionDTO.getOrderDisplay());
                    option.setQuestion(savedQuestion);
                    return option;
                }).toList();

                // Batch save options
                this.optionRepository.saveAll(options);
                savedQuestion.setOptions(options);
            }

            return savedQuestion;
        }).toList();

        savedPost.setQuestions(questions);
        return savedPost;
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
