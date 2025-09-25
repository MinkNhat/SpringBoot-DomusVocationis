package vn.nmn.domusvocationis.service;

import org.springframework.stereotype.Service;
import vn.nmn.domusvocationis.domain.Answer;
import vn.nmn.domusvocationis.domain.Option;
import vn.nmn.domusvocationis.domain.Post;
import vn.nmn.domusvocationis.domain.Question;
import vn.nmn.domusvocationis.domain.response.post.ResSurveyStatsDTO;
import vn.nmn.domusvocationis.repository.AnswerRepository;
import vn.nmn.domusvocationis.repository.OptionRepository;
import vn.nmn.domusvocationis.repository.PostRepository;
import vn.nmn.domusvocationis.repository.QuestionRepository;
import vn.nmn.domusvocationis.util.error.IdInvalidException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SurveyStatsService {
    private final PostRepository postRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final OptionRepository optionRepository;

    public SurveyStatsService(PostRepository postRepository, QuestionRepository questionRepository, AnswerRepository answerRepository, OptionRepository optionRepository) {
        this.postRepository = postRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.optionRepository = optionRepository;
    }

    public ResSurveyStatsDTO getChartStats(Long postId) throws IdInvalidException {
        Post post = postRepository.findById(postId).orElse(null);
        if(post == null) throw new IdInvalidException("Không tìm thấy bài viết với id = " + postId);

        List<Answer> allAnswers = answerRepository.findByQuestion_PostId(postId);
        long totalParticipants = allAnswers.stream().map(answer -> answer.getUser().getId()).distinct().count();

        ResSurveyStatsDTO res = new ResSurveyStatsDTO();
        res.setPostId(postId);
        res.setTitle(post.getTitle());
        res.setTotalParticipants((int) totalParticipants);

        List<Question> questions = questionRepository.findByPostIdOrderByOrderDisplay(postId);
        List<ResSurveyStatsDTO.QuestionStatsDTO> questionStats = questions.stream().map(this::buildQuestionStats).toList();
        res.setQuestionsStats(questionStats);

        return res;
    }


    private ResSurveyStatsDTO.QuestionStatsDTO buildQuestionStats(Question question) {
        Long questionId = question.getId();

        List<Answer> answers = answerRepository.findByQuestionId(questionId);
        int totalAnswers = answers.size();

        List<ResSurveyStatsDTO.QuestionStatsDTO.ChartDataDTO> chartData = buildChartData(questionId, totalAnswers, answers);
        return new ResSurveyStatsDTO.QuestionStatsDTO(
                questionId,
                question.getQuestionText(),
                question.getType(),
                totalAnswers,
                chartData
        );
    }

    private List<ResSurveyStatsDTO.QuestionStatsDTO.ChartDataDTO> buildChartData(Long questionId, int totalAnswers, List<Answer> answers) {
        List<Option> options = optionRepository.findByQuestionIdOrderByOrderDisplay(questionId);

        Map<Long, Long> optionCounts = answers.stream()
                .flatMap(answer -> answer.getSelectedOptions().stream())
                .collect(Collectors.groupingBy(Option::getId, Collectors.counting()));

        List<ResSurveyStatsDTO.QuestionStatsDTO.ChartDataDTO> chartData = new ArrayList<>();

        for (Option option : options) {
            long count = optionCounts.getOrDefault(option.getId(), 0L);

            // percent
            BigDecimal percentage = BigDecimal.ZERO;
            if (totalAnswers > 0) {
                percentage = BigDecimal.valueOf(count)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalAnswers), 2, RoundingMode.HALF_UP);
            }

            chartData.add(new ResSurveyStatsDTO.QuestionStatsDTO.ChartDataDTO(option.getId(), option.getOptionText(), (int)count, percentage));
        }

        return chartData;
    }
}
