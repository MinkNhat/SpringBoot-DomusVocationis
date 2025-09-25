package vn.nmn.domusvocationis.schedule.trigger;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.nmn.domusvocationis.repository.PeriodRepository;
import vn.nmn.domusvocationis.schedule.job.ClosePeriodJob;
import vn.nmn.domusvocationis.schedule.job.OpenPeriodJob;
import vn.nmn.domusvocationis.util.constant.PeriodStatusEnum;

@Service
public class QuartzSchedulerService {

    private final Scheduler scheduler;
    private final PeriodRepository periodRepository;

    public QuartzSchedulerService(Scheduler scheduler, PeriodRepository periodRepository) {
        this.scheduler = scheduler;
        this.periodRepository = periodRepository;
    }

    public void scheduleOpenPeriodJob(Long periodId) throws SchedulerException {
        deleteJobAndTrigger(periodId, "openPeriod");

        var period = periodRepository.findById(periodId).orElseThrow(() -> new IllegalArgumentException("Period not found: " + periodId));
        if (period.getStatus() == PeriodStatusEnum.OPENING) return;

        JobDetail jobDetail = JobBuilder.newJob(OpenPeriodJob.class)
                .withIdentity("openPeriodJob_" + periodId, "periodGroup")
                .usingJobData("periodId", periodId)
                .storeDurably()
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("openPeriodTrigger_" + periodId, "periodGroup")
                .startAt(java.util.Date.from(period.getRegistrationStartTime()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void scheduleClosePeriodJob(Long periodId) throws SchedulerException {
        deleteJobAndTrigger(periodId, "closePeriod");

        var period = periodRepository.findById(periodId).orElseThrow(() -> new IllegalArgumentException("Period not found: " + periodId));
        if (period.getStatus() == PeriodStatusEnum.CLOSED) return;

        JobDetail jobDetail = JobBuilder.newJob(ClosePeriodJob.class)
                .withIdentity("closePeriodJob_" + periodId, "periodGroup")
                .usingJobData("periodId", periodId)
                .storeDurably()
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("closePeriodTrigger_" + periodId, "periodGroup")
                .startAt(java.util.Date.from(period.getRegistrationEndTime()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void deletePeriodJobs(Long periodId) throws SchedulerException {
        deleteJobAndTrigger(periodId, "openPeriod");
        deleteJobAndTrigger(periodId, "closePeriod");
    }

    private void deleteJobAndTrigger(Long periodId, String jobTypePrefix) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobTypePrefix + "Trigger_" + periodId, "periodGroup");
        JobKey jobKey = JobKey.jobKey(jobTypePrefix + "Job_" + periodId, "periodGroup");

        if (scheduler.checkExists(triggerKey)) {
            scheduler.unscheduleJob(triggerKey);
        }

        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }
    }
}