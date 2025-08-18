package vn.nmn.domusvocationis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.nmn.domusvocationis.domain.SchedulePeriod;
import vn.nmn.domusvocationis.util.constant.SchedulePeriodStatusEnum;
import vn.nmn.domusvocationis.util.constant.ScheduleTypeEnum;

@Repository
public interface SchedulePeriodRepository extends JpaRepository<SchedulePeriod, Long>, JpaSpecificationExecutor<SchedulePeriod> {
    SchedulePeriod findByStatusAndType(SchedulePeriodStatusEnum status, ScheduleTypeEnum type);
}
