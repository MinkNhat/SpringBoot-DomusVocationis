package vn.nmn.domusvocationis.domain.response.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResAnswerDTO {
    private Long id;
    private String answerText;
    private QuestionAnswer question;
    private UserAnswer user;
    private List<OptionAnswer> selectedOptions;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserAnswer {
        private long id;
        private String full_name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class QuestionAnswer {
        private long id;
        private String questionText;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class OptionAnswer {
        private long id;
        private String optionText;
    }
}
