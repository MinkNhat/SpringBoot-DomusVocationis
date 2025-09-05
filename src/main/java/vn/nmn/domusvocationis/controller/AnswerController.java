package vn.nmn.domusvocationis.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.nmn.domusvocationis.domain.Answer;
import vn.nmn.domusvocationis.domain.response.post.ResAnswerDTO;
import vn.nmn.domusvocationis.service.AnswerService;
import vn.nmn.domusvocationis.util.annotation.ApiMessage;
import vn.nmn.domusvocationis.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class AnswerController {
    private final AnswerService answerService;

    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping("/answers")
    @ApiMessage("Create a answer")
    public ResponseEntity<ResAnswerDTO> create(@Valid @RequestBody Answer answer) throws IdInvalidException {
        Answer ans = this.answerService.create(answer);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.answerService.convertToResAnswerDTO(ans));
    }
}
