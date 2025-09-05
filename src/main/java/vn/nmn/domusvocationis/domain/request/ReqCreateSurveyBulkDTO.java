package vn.nmn.domusvocationis.domain.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.nmn.domusvocationis.util.constant.PostStatusEnum;
import vn.nmn.domusvocationis.util.constant.PostTypeEnum;
import vn.nmn.domusvocationis.util.constant.QuestionTypeEnum;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ReqCreateSurveyBulkDTO {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    private String content;
    private PostTypeEnum type;
    private PostStatusEnum status;
    private Instant expiresAt;
    private Boolean publicPost;

    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;

    @Valid
    @NotEmpty(message = "Danh sách câu hỏi không được để trống")
    private List<QuestionDTO> questions;

    @Getter
    @Setter
    public static class QuestionDTO {
        @NotBlank(message = "Câu hỏi không được để trống")
        private String questionText;

        @NotNull(message = "Loại câu hỏi không được để trống")
        private QuestionTypeEnum type;

        @NotNull(message = "Thứ tự hiển thị không được để trống")
        private Integer orderDisplay;
        private Boolean required = false;
        private Boolean allowMultiple = false;

        @Valid
        private List<OptionDTO> options;
    }

    @Getter
    @Setter
    public static class OptionDTO {
        @NotBlank(message = "Lựa chọn không được để trống")
        private String optionText;

        @NotNull(message = "Thứ tự hiển thị không được để trống")
        private Integer orderDisplay;
    }
}