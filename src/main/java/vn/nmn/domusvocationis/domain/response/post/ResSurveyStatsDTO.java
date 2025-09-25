package vn.nmn.domusvocationis.domain.response.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.nmn.domusvocationis.util.constant.QuestionTypeEnum;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResSurveyStatsDTO {
    private long postId;
    private String title;
    private int totalParticipants;
    private List<QuestionStatsDTO> questionsStats;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class QuestionStatsDTO {
        private long questionId;
        private String questionText;
        private QuestionTypeEnum type;
        private int totalAnswers;
        private List<ChartDataDTO> chartData;

        @Getter
        @Setter
        @AllArgsConstructor
        public static class ChartDataDTO {
            private long optionId;
            private String label;
            private int count;
            private BigDecimal percentage;
        }

    }
}
