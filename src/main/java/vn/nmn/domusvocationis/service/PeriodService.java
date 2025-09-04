package vn.nmn.domusvocationis.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.nmn.domusvocationis.domain.Period;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.domain.response.schedule.ResSessionByPeriodDTO;
import vn.nmn.domusvocationis.repository.PeriodRepository;
import vn.nmn.domusvocationis.repository.SessionRepository;
import vn.nmn.domusvocationis.repository.UserRepository;
import vn.nmn.domusvocationis.util.DateTimeUtil;
import vn.nmn.domusvocationis.util.constant.PeriodStatusEnum;
import vn.nmn.domusvocationis.util.constant.SessionTimeEnum;
import vn.nmn.domusvocationis.util.error.IdInvalidException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PeriodService {
    private final PeriodRepository periodRepository;

    private final SessionRepository slotRepository;

    private final UserRepository userRepository;

    public PeriodService(PeriodRepository periodRepository, SessionRepository slotRepository, UserRepository userRepository, DateTimeUtil dateTimeUtil) {
        this.periodRepository = periodRepository;
        this.slotRepository = slotRepository;
        this.userRepository = userRepository;
    }

    public ResSessionByPeriodDTO getSessionByPeriod(Period period) {
        ResSessionByPeriodDTO res = new ResSessionByPeriodDTO();
        res.setId(period.getId());

        List<ResSessionByPeriodDTO.Session> listSession = new ArrayList<>();

        period.getSessions().forEach(s -> {
            List<ResSessionByPeriodDTO.Session.UserSession> listUser = new ArrayList<>();
            s.getUsers().forEach(u -> {
                    ResSessionByPeriodDTO.Session.UserSession resUser = new ResSessionByPeriodDTO.Session.UserSession(u.getId(), u.getFullName());
                    listUser.add(resUser);
            });

            ResSessionByPeriodDTO.Session resSession = new ResSessionByPeriodDTO.Session(s.getId(), s.getRegistrationDate(), s.getSessionTime(), s.getActivity(), s.getTotalSlot(), listUser);
            listSession.add(resSession);
        });

        res.setSessions(listSession);
        return res;
    }

    public Period getPeriodById(Long id) {
        return periodRepository.findById(id).orElse(null);
    }

    public ResPaginationDTO getListPeriods(Specification<Period> spec, Pageable pageable) {
        Page<Period> periodPage = this.periodRepository.findAll(spec, pageable);
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

    public ResPaginationDTO getOpenPeriods(Pageable pageable) {
        Instant now = Instant.now();

        Specification<Period> spec = (root, query, cb) -> cb.and(
                cb.equal(root.get("status"), PeriodStatusEnum.OPENING),
                cb.lessThanOrEqualTo(root.get("registrationStartTime"), now),
                cb.greaterThanOrEqualTo(root.get("registrationEndTime"), now)
        );

        Page<Period> periodPage = periodRepository.findAll(spec, pageable);
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

    public Period createPeriod(Period period) {
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

        if(period.getStatus() != null && period.getStatus() == PeriodStatusEnum.OPENING) {
            period.setRegistrationStartTime(Instant.now());
        }

        if(period.getName() == null || period.getName().isEmpty())
            period.setName(String.format("Phiên %s (%s -> %s)", period.getType(), period.getStartDate(), period.getEndDate()));

        return this.periodRepository.save(period);
    }

    public Period updatePeriod(Period period, Period dbPeriod) throws IdInvalidException {
        if(period.getName() == null || period.getName().isEmpty()) {
            dbPeriod.setName(String.format("%s PERIOD (%s -> %s)", dbPeriod.getType(), dbPeriod.getStartDate(), dbPeriod.getEndDate()));
        } else {
            dbPeriod.setName(period.getName());
        }

        dbPeriod.setRegistrationStartTime(period.getRegistrationStartTime());
        dbPeriod.setRegistrationEndTime(period.getRegistrationEndTime());

        if(period.getStatus() != null && period.getStatus() == PeriodStatusEnum.OPENING) {
            dbPeriod.setRegistrationStartTime(Instant.now());
        } else if(period.getStatus() != null && period.getStatus() == PeriodStatusEnum.CLOSED) {
            dbPeriod.setRegistrationEndTime(Instant.now());
        }

        dbPeriod.setStatus(period.getStatus());
        dbPeriod.setNotes(period.getNotes());

        if(!dbPeriod.getRegistrationStartTime().isBefore(dbPeriod.getRegistrationEndTime())) {
            throw new IdInvalidException(String.format("Thời gian bắt đầu đăng ký (%s) phải nhỏ hơn thời gian kết thúc (%s)", dbPeriod.getRegistrationStartTime(), dbPeriod.getRegistrationEndTime()));
        }

        return this.periodRepository.save(dbPeriod);
    }

    public void deletePeriod(Long id) {
        Period period = this.periodRepository.findById(id).orElse(null);
        this.slotRepository.deleteByPeriod(period);
        this.periodRepository.deleteById(id);
    }

//    private List<LocalDate> generateDates(LocalDate startDate, LocalDate endDate, Set<Integer> excludedDaysOfWeek) {
//        List<LocalDate> dates = new ArrayList<>();
//        LocalDate currentDate = startDate;
//
//        while (!currentDate.isAfter(endDate)) {
//            int dayValue = currentDate.getDayOfWeek().getValue() % 7; // Sunday = 0
//
//            if (!excludedDaysOfWeek.contains(dayValue)) {
//                dates.add(currentDate);
//            }
//            currentDate = currentDate.plusDays(1);
//        }
//
//        return dates;
//    }

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
