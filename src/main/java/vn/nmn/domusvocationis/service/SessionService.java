package vn.nmn.domusvocationis.service;

import org.springframework.stereotype.Service;
import vn.nmn.domusvocationis.domain.Period;
import vn.nmn.domusvocationis.domain.Session;
import vn.nmn.domusvocationis.domain.User;
import vn.nmn.domusvocationis.domain.response.schedule.ResSessionDTO;
import vn.nmn.domusvocationis.repository.PeriodRepository;
import vn.nmn.domusvocationis.repository.SessionRepository;
import vn.nmn.domusvocationis.repository.UserRepository;
import vn.nmn.domusvocationis.util.SecurityUtil;
import vn.nmn.domusvocationis.util.constant.PeriodStatusEnum;
import vn.nmn.domusvocationis.util.error.IdInvalidException;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SessionService {
    private final PeriodRepository periodRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public SessionService(PeriodRepository periodRepository, SessionRepository sessionRepository, UserRepository userRepository) {
        this.periodRepository = periodRepository;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    public Session getSessionById(Long id) {
        return this.sessionRepository.findById(id).orElse(null);
    }

    public ResSessionDTO convertToResSessionDTO(Session session) {
        ResSessionDTO res = new ResSessionDTO();
        res.setId(session.getId());
        res.setRegistrationDate(session.getRegistrationDate());
        res.setSessionTime(session.getSessionTime());
        res.setActivity(session.getActivity());
        res.setTotalSlot(session.getTotalSlot());
        res.setCreatedAt(session.getCreatedAt());
        res.setUpdatedAt(session.getUpdatedAt());

        res.setPeriod(new ResSessionDTO.PeriodSession(
                session.getPeriod().getId(),
                session.getPeriod().getName(),
                session.getPeriod().getStatus()
        ));

        if(session.getUsers() != null) {
            res.setUsers(session.getUsers().stream().map(u -> new ResSessionDTO.UserSession(u.getId(), u.getFullName())).toList());
        } else {
            res.setUsers(new ArrayList<>());
        }

        return res;
    }
    
    public ResSessionDTO createSession(Session session) {
        Period period = this.periodRepository.findById(session.getPeriod().getId()).orElse(null);
        if(period != null) {
            session.setPeriod(period);

            LocalDate sessionDate = session.getRegistrationDate();

            if (sessionDate.isBefore(period.getStartDate()) || sessionDate.isAfter(period.getEndDate())) {
                throw new IllegalArgumentException("Ngày của session phải nằm trong khoảng từ " + period.getStartDate() + " đến " + period.getEndDate());
            }

            DayOfWeek dayOfWeek = sessionDate.getDayOfWeek();
            if (period.getExcludedDaysOfWeek().contains(dayOfWeek.getValue())) {
                throw new IllegalArgumentException("Ngày trong tuần của session không được trùng với ngày bị loại bỏ trong tuần.");
            }
        }

        Session s = this.sessionRepository.save(session);
        return convertToResSessionDTO(s);
    }

    public ResSessionDTO registerSession(Session session) throws IdInvalidException {
        Period period = this.periodRepository.findById(session.getPeriod().getId()).orElseThrow(() -> new IdInvalidException("Phiên đăng ký không tồn tại"));

        Instant now = Instant.now();
        if (period.getStatus() != PeriodStatusEnum.OPENING || now.isBefore(period.getRegistrationStartTime()) || now.isAfter(period.getRegistrationEndTime()))
            throw new IllegalStateException("Phiên đăng ký chưa sẵn sàng");

        if (!period.getAllowedSessions().contains(session.getSessionTime()))
            throw new IllegalStateException("Buổi không được phép đăng ký trong phiên này");

        if (session.getUsers().size() >= session.getTotalSlot())
            throw new IllegalStateException("session đã đầy");

        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User currentUser = this.userRepository.findByEmail(email);

//        List<Session> userSessionsInSameDate = sessionRepository.findByPeriodAndRegistrationDateAndSessionTimeAndUsersContaining(period, session.getRegistrationDate(), session.getSessionTime(), currentUser);
//        if (!userSessionsInSameDate.isEmpty()) {
//            throw new IllegalStateException(String.format("Bạn đã đăng ký buổi %s trong ngày %s rồi", session.getSessionTime().getDisplayName().toLowerCase(), session.getRegistrationDate()));
//        }

        session.getUsers().add(currentUser);

        Session currentsession = this.sessionRepository.save(session);
        return this.convertToResSessionDTO(currentsession);
    }
}
