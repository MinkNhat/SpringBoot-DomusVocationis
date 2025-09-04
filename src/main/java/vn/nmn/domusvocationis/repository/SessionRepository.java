package vn.nmn.domusvocationis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.nmn.domusvocationis.domain.Period;
import vn.nmn.domusvocationis.domain.Session;

@Transactional
@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    void deleteByPeriod(Period period);
//    List<Session> findByPeriodAndRegistrationDateAndSessionTimeAndUsersContaining(Period period, LocalDate registrationDate, SessionTimeEnum sessionTime, User user);
}
