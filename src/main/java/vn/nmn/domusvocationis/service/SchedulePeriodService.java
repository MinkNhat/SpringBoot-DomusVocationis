package vn.nmn.domusvocationis.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vn.nmn.domusvocationis.domain.Role;
import vn.nmn.domusvocationis.domain.SchedulePeriod;
import vn.nmn.domusvocationis.domain.ScheduleSlot;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.repository.SchedulePeriodRepository;
import vn.nmn.domusvocationis.repository.ScheduleSlotRepository;
import vn.nmn.domusvocationis.repository.UserRepository;
import vn.nmn.domusvocationis.util.DateTimeUtil;
import vn.nmn.domusvocationis.util.constant.SchedulePeriodStatusEnum;
import vn.nmn.domusvocationis.util.constant.SessionTimeEnum;

import java.time.temporal.ChronoUnit;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SchedulePeriodService {
    private final SchedulePeriodRepository periodRepository;

    private final ScheduleSlotRepository slotRepository;

    private final UserRepository userRepository;

    private final DateTimeUtil dateTimeUtil;

    public SchedulePeriodService(SchedulePeriodRepository periodRepository, ScheduleSlotRepository slotRepository, UserRepository userRepository, DateTimeUtil dateTimeUtil) {
        this.periodRepository = periodRepository;
        this.slotRepository = slotRepository;
        this.userRepository = userRepository;
        this.dateTimeUtil = dateTimeUtil;
    }

    public SchedulePeriod getPeriodById(Long id) {
        return periodRepository.findById(id).orElse(null);
    }

    public ResPaginationDTO getListPeriods(Specification<SchedulePeriod> spec, Pageable pageable) {
        Page<SchedulePeriod> periodPage = this.periodRepository.findAll(spec, pageable);
        ResPaginationDTO rs = new ResPaginationDTO();
        ResPaginationDTO.Meta mt = new ResPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(periodPage.getTotalPages());
        mt.setTotal(periodPage.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(periodPage.getContent());

        return rs;
    }

    public SchedulePeriod createPeriod(SchedulePeriod period) {
        if (period.getExcludedDaysOfWeek() != null) {
            for (Integer day : period.getExcludedDaysOfWeek()) {
                if (day < 0 || day > 6) {
                    throw new IllegalArgumentException("Giá trị ngày trong tuần phải từ 0 (Chủ nhật) đến 6 (Thứ 7)");
                }
            }
        }
        else period.setExcludedDaysOfWeek(new HashSet<>());

        if (period.getAllowedSessions() == null || period.getAllowedSessions().isEmpty()) {
            period.setAllowedSessions(Set.of(SessionTimeEnum.ALL_DAY));
        }

        if(period.getStatus() != null && period.getStatus() == SchedulePeriodStatusEnum.OPENING)
            period.setRegistrationStartTime(Instant.now());
        else period.setStatus(SchedulePeriodStatusEnum.PENDING);

        // số ngày = số lượng users có role là null ( người dùng thường ) đang active
//        int numberOfDays = (int) userRepository.countByActiveAndRole(true, null);


        List<Instant> registrationDates = generateDates(period.getStartDate(), period.getEndDate(), period.getExcludedDaysOfWeek());

        int totalSlotsNeeded = registrationDates.size() * period.getAllowedSessions().size() * period.getPeoplePerSession();
        if (period.getMaxSlots() != null) {
            if (period.getMaxSlots() > totalSlotsNeeded || period.getMaxSlots() <= 0)
                throw new IllegalArgumentException("Số slots tối đa không hợp lệ (Phải nhỏ hơn hoặc bằng " + totalSlotsNeeded + ")");
        } else {
            period.setMaxSlots(totalSlotsNeeded);
        }

        period.setName(String.format("%s PERIOD (%s -> %s)", period.getType(), dateTimeUtil.formatInstant(period.getStartDate()), dateTimeUtil.formatInstant(period.getEndDate())));
        period = this.periodRepository.save(period); //save trước để slot tham chiếu đến period không lỗi

        List<ScheduleSlot> slots = new ArrayList<>();
        for (Instant date : registrationDates) {
            for (SessionTimeEnum sessionTime : period.getAllowedSessions()) {
                ScheduleSlot slot = new ScheduleSlot();
                slot.setRegistrationDate(date);
                slot.setSessionTime(sessionTime);
                slot.setPeriod(period);
                slots.add(slot);
            }
        }
        this.slotRepository.saveAll(slots);

        return period;
    }

    private List<Instant> generateDates(Instant startDate, Instant endDate, Set<Integer> excludedDaysOfWeek) {
        List<Instant> dates = new ArrayList<>();
        Instant currentDate = startDate.truncatedTo(ChronoUnit.DAYS);
        Instant end = endDate.truncatedTo(ChronoUnit.DAYS);

        while (!currentDate.isAfter(end)) {
            LocalDate localDate = currentDate.atZone(ZoneOffset.UTC).toLocalDate();
            int dayValue = localDate.getDayOfWeek().getValue() % 7; // Sunday = 0

            if (!excludedDaysOfWeek.contains(dayValue)) {
                dates.add(currentDate);
            }
            currentDate = currentDate.plus(1, ChronoUnit.DAYS);
        }

        return dates;
    }

//    @Scheduled(cron = "0 0 0 * * ?") // Run daily at midnight
//    public void updatePeriodStatus() {
//        List<SchedulePeriod> periods = periodRepository.findAll();
//        Instant now = Instant.now();
//
//        for (SchedulePeriod period : periods) {
//            if (period.getStatus() == SchedulePeriodStatusEnum.PENDING && now.isAfter(period.getStartDate())) {
//                period.setStatus(SchedulePeriodStatusEnum.OPENING);
//                periodRepository.save(period);
//            } else if (period.getStatus() == SchedulePeriodStatusEnum.OPENING && now.isAfter(period.getEndDate())) {
//                period.setStatus(SchedulePeriodStatusEnum.CLOSED);
//                periodRepository.save(period);
//            }
//        }
//    }

}
