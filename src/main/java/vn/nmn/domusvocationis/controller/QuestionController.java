package vn.nmn.domusvocationis.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.nmn.domusvocationis.domain.Question;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.service.QuestionService;
import vn.nmn.domusvocationis.util.annotation.ApiMessage;
import vn.nmn.domusvocationis.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class QuestionController {
    private final QuestionService questionService;
    
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }
//
//    @GetMapping("/questions/{id}")
//    @ApiMessage("get a question")
//    public ResponseEntity<Question> getQuestionById(@PathVariable Long id) throws IdInvalidException {
//        Question question = questionService.getQuestionById(id);
//        if (question == null) {
//            throw new IdInvalidException("Question có id = " + id + " không tồn tại");
//        }
//        return ResponseEntity.ok(question);
//    }
//
//    @GetMapping("/questions")
//    @ApiMessage("Fetch questions")
//    public ResponseEntity<ResPaginationDTO> getListCategories(@Filter Specification<Question> spec, Pageable pageable) {
//        return ResponseEntity.ok(this.questionService.getListCategories(spec, pageable));
//    }
//
    @PostMapping("/questions")
    @ApiMessage("Create a question")
    public ResponseEntity<Question> create(@Valid @RequestBody Question question) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.questionService.create(question));
    }

//    @PutMapping("/questions")
//    @ApiMessage("Update a question")
//    public ResponseEntity<Question> update(@Valid @RequestBody Question question) throws IdInvalidException {
//        Question dbQuestion = questionService.getQuestionById(question.getId());
//        if (dbQuestion == null) {
//            throw new IdInvalidException("Question có id = " + question.getId() + " không tồn tại");
//        }
//        return ResponseEntity.status(HttpStatus.OK).body(this.questionService.update(question, dbQuestion));
//    }
//
//    @DeleteMapping("/questions/{id}")
//    @ApiMessage("Delete a question")
//    public ResponseEntity<Void> delete(@PathVariable Long id) throws IdInvalidException {
//        Question question = questionService.getQuestionById(id);
//        if (question == null) {
//            throw new IdInvalidException("Question có id = " + id + " không tồn tại");
//        }
//
//        this.questionService.delete(id);
//        return ResponseEntity.ok(null);
//    }
}
