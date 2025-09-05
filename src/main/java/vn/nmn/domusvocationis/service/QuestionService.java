package vn.nmn.domusvocationis.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.nmn.domusvocationis.domain.Question;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.repository.QuestionRepository;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question getQuestionById(Long id) {
        return this.questionRepository.findById(id).orElse(null);
    }

    public ResPaginationDTO getListQuestions(Specification<Question> spec, Pageable pageable) {
        Page<Question> questionPage = this.questionRepository.findAll(spec, pageable);
        ResPaginationDTO rs = new ResPaginationDTO();
        ResPaginationDTO.Meta mt = new ResPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(questionPage.getTotalPages());
        mt.setTotal(questionPage.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(questionPage.getContent());

        return rs;
    }

    public Question create(Question question) {
        return this.questionRepository.save(question);
    }

//    public Question update(Question question, Question dbQuestion) {
//        if(dbQuestion != null) {
//            dbQuestion.setName(cate.getName());
//            dbQuestion.setDescription(cate.getDescription());
//            dbQuestion.setActive(cate.isActive());
//            dbQuestion.setAllowPost(cate.isAllowPost());
//            return this.questionRepository.save(dbQuestion);
//        }
//
//        return null;
//    }
//
//    public void delete(Long id) {
//        Question cate = this.getQuestionById(id);
//        this.postRepository.deleteByQuestion(cate);
//        this.questionRepository.deleteById(id);
//    }
}
