package vn.nmn.domusvocationis.service;

import org.springframework.stereotype.Service;
import vn.nmn.domusvocationis.domain.*;
import vn.nmn.domusvocationis.domain.response.post.ResAnswerDTO;
import vn.nmn.domusvocationis.repository.AnswerRepository;
import vn.nmn.domusvocationis.repository.OptionRepository;
import vn.nmn.domusvocationis.repository.QuestionRepository;
import vn.nmn.domusvocationis.repository.UserRepository;
import vn.nmn.domusvocationis.util.SecurityUtil;
import vn.nmn.domusvocationis.util.error.IdInvalidException;

import java.time.Instant;
import java.util.List;

@Service
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;


    public AnswerService(AnswerRepository answerRepository, QuestionRepository questionRepository, OptionRepository optionRepository, UserRepository userRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.userRepository = userRepository;
    }

    public ResAnswerDTO convertToResAnswerDTO(Answer answer) {
        ResAnswerDTO res = new ResAnswerDTO();

        res.setId(answer.getId());
        res.setAnswerText(answer.getAnswerText());

        if(answer.getUser() != null) {
            ResAnswerDTO.UserAnswer user = new ResAnswerDTO.UserAnswer(answer.getUser().getId(), answer.getUser().getFullName());
            res.setUser(user);
        }

        if(answer.getQuestion() != null) {
            ResAnswerDTO.QuestionAnswer question = new ResAnswerDTO.QuestionAnswer(answer.getQuestion().getId(), answer.getQuestion().getQuestionText());
            res.setQuestion(question);
        }

        if(answer.getSelectedOptions() != null) {
            List<ResAnswerDTO.OptionAnswer> options = answer.getSelectedOptions().stream().map(opt -> new ResAnswerDTO.OptionAnswer(opt.getId(), opt.getOptionText())).toList();
            res.setSelectedOptions(options);
        }

        return res;
    }

    public Answer create(Answer answer) throws IdInvalidException {
        Question question = this.questionRepository.findById(answer.getQuestion().getId()).orElse(null);
        if(question == null) throw new IdInvalidException("Câu hỏi có id = " + answer.getQuestion().getId() + " không tồn tại");

        Instant now = Instant.now();
        if(now.isAfter(question.getPost().getExpiresAt())) {
            throw new IdInvalidException("Đã hết hạn trả lời câu hỏi");
        }

        if(answer.getSelectedOptions() != null) {
            List<Long> reqOpt = answer.getSelectedOptions().stream().map(p -> p.getId()).toList();
            List<Option> dbOpt = this.optionRepository.findByIdIn(reqOpt);
            answer.setSelectedOptions(dbOpt);
        }

        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User currentUser = this.userRepository.findByEmail(email);
        answer.setUser(currentUser);

        if(this.answerRepository.findByUserIdAndQuestionId(currentUser.getId(), answer.getQuestion().getId()) != null) {
            throw new IdInvalidException("Bạn đã trả lời câu hỏi \"" + question.getQuestionText() + "\" rồi!");
        }

        return this.answerRepository.save(answer);
    }
}
