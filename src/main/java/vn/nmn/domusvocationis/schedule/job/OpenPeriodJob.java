package vn.nmn.domusvocationis.schedule.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.nmn.domusvocationis.domain.Period;
import vn.nmn.domusvocationis.repository.PeriodRepository;
import vn.nmn.domusvocationis.util.constant.PeriodStatusEnum;

import java.time.Instant;

@Component
public class OpenPeriodJob implements Job {
    private final PeriodRepository periodRepository;

    public OpenPeriodJob(PeriodRepository periodRepository) {
        this.periodRepository = periodRepository;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long periodId = context.getJobDetail().getJobDataMap().getLong("periodId");
        if (periodId == null) {
            throw new JobExecutionException("PeriodId is required");
        }

        Period period = periodRepository.findById(periodId).orElse(null);
        if (period == null) {
            return;
        }

        Instant now = Instant.now();
        if (period.getStatus() != PeriodStatusEnum.OPENING && !now.isBefore(period.getRegistrationStartTime())) {
            period.setStatus(PeriodStatusEnum.OPENING);
            periodRepository.save(period);
        }
    }
}
