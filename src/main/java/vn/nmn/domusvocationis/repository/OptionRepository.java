package vn.nmn.domusvocationis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.nmn.domusvocationis.domain.Option;
import vn.nmn.domusvocationis.domain.Permission;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long>, JpaSpecificationExecutor<Option> {
    List<Option> findByIdIn(List<Long> id);
    List<Option> findByQuestionIdOrderByOrderDisplay(Long question_id);
}
