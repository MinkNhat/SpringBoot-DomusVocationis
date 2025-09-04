package vn.nmn.domusvocationis.domain.response.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.nmn.domusvocationis.util.constant.PostStatusEnum;
import vn.nmn.domusvocationis.util.constant.PostTypeEnum;

import java.time.Instant;

@Getter
@Setter
public class ResPostDTO {
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
}
