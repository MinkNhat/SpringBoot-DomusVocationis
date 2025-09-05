package vn.nmn.domusvocationis.domain.response.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.nmn.domusvocationis.util.constant.PostStatusEnum;
import vn.nmn.domusvocationis.util.constant.PostTypeEnum;
import vn.nmn.domusvocationis.util.constant.QuestionTypeEnum;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ResGetPostByIdDTO {
    private Long id;
    private String title;
    private String content;
    private PostTypeEnum type;
    private PostStatusEnum status;
    private Instant expiresAt;
    private Boolean publicPost;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;

    private UserPost user;
    private CategoryPost category;
    private List<QuestionPost> questions;

    private boolean isSubmitted;


    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserPost {
        private long id;
        private String full_name;
        private String avatar;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class CategoryPost {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    public static class QuestionPost {
        private long id;
        private String questionText;
        private QuestionTypeEnum type;
        private Integer orderDisplay;
        private Boolean required = false;
        private Boolean allowMultiple = false;
        private List<OptionQuestion> options;
    }

    @Getter
    @Setter
    public static class OptionQuestion {
        private long id;
        private String optionText;
        private Integer orderDisplay;
    }
}

