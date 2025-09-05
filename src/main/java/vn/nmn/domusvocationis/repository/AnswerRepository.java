package vn.nmn.domusvocationis.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.nmn.domusvocationis.domain.Answer;
import vn.nmn.domusvocationis.domain.Question;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long>, JpaSpecificationExecutor<Answer> {
    Answer findByUserIdAndQuestionId(Long user_id, Long question_id);
    boolean existsByQuestion_Post_IdAndUser_Id(Long question_post_id, Long user_id);
}
