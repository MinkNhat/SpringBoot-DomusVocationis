package vn.nmn.domusvocationis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.nmn.domusvocationis.domain.Period;
import vn.nmn.domusvocationis.util.constant.PeriodStatusEnum;
import vn.nmn.domusvocationis.util.constant.PeriodTypeEnum;

@Repository
public interface PeriodRepository extends JpaRepository<Period, Long>, JpaSpecificationExecutor<Period> {
    Period findByStatusAndType(PeriodStatusEnum status, PeriodTypeEnum type);
}
