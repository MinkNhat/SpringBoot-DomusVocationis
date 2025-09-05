package vn.nmn.domusvocationis.domain.response.post;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.nmn.domusvocationis.util.constant.PostStatusEnum;
import vn.nmn.domusvocationis.util.constant.PostTypeEnum;
import vn.nmn.domusvocationis.util.constant.QuestionTypeEnum;

import java.time.Instant;

@Getter
@Setter
public class ResQuestionDTO {
    private Long id;
    private String questionText;
    private QuestionTypeEnum type;
    private Integer orderDisplay;
    private Boolean required;
    private Boolean allowMultiple;

    private Instant createdAt;
    private Instant updatedAt;

    private PostQuestion post;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class PostQuestion {
        private long id;
        private String title;
        private Instant expiresAt;
    }

}

