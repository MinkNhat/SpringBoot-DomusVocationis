package vn.nmn.domusvocationis.schedule.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import vn.nmn.domusvocationis.domain.Period;
import vn.nmn.domusvocationis.repository.PeriodRepository;
import vn.nmn.domusvocationis.util.constant.PeriodStatusEnum;

import java.time.Instant;

@Component
public class ClosePeriodJob implements Job {

    private final PeriodRepository periodRepository;

    public ClosePeriodJob(PeriodRepository periodRepository) {
        this.periodRepository = periodRepository;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long periodId = context.getJobDetail().getJobDataMap().getLong("periodId");
        if (periodId == null) {
            throw new JobExecutionException("Period id is required");
        }

        Period period = periodRepository.findById(periodId).orElse(null);
        if (period == null) {
            return;
        }

        Instant now = Instant.now();
        if (period.getStatus() != PeriodStatusEnum.CLOSED && !now.isBefore(period.getRegistrationEndTime())) {
            period.setStatus(PeriodStatusEnum.CLOSED);
            periodRepository.save(period);
        }
    }
}
