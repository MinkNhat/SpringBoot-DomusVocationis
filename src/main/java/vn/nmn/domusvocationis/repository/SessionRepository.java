package vn.nmn.domusvocationis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import vn.nmn.domusvocationis.domain.Period;
import vn.nmn.domusvocationis.domain.Session;
import vn.nmn.domusvocationis.domain.User;
import vn.nmn.domusvocationis.util.constant.SessionTimeEnum;

import java.time.LocalDate;
import java.util.List;

@Transactional
public interface SessionRepository extends JpaRepository<Session, Long> {
    void deleteByPeriod(Period period);
    List<Session> findByPeriodAndRegistrationDateAndSessionTimeAndUsersContaining(Period period, LocalDate registrationDate, SessionTimeEnum sessionTime, User user);


//    List<ScheduleSlot> findByPeriod(SchedulePeriod period);
//    List<ScheduleSlot> findByUsersContaining(User user);
//    List<ScheduleSlot> findByPeriodAndUsersContaining(SchedulePeriod period, User user);
//
//    @Query("SELECT s FROM ScheduleSlot s WHERE s.period.id = :periodId AND SIZE(s.users) < s.period.peoplePerSession")
//    List<ScheduleSlot> findAvailableSlots(@Param("periodId") Long periodId);
//
//    // Find available slots by period
//    @Query("SELECT s FROM ScheduleSlot s WHERE s.period = :period AND SIZE(s.users) < s.period.peoplePerSession")
//    List<ScheduleSlot> findAvailableSlotsByPeriod(@Param("period") SchedulePeriod period);
//
//    // Find full slots by period
//    @Query("SELECT s FROM ScheduleSlot s WHERE s.period = :period AND SIZE(s.users) >= s.period.peoplePerSession")
//    List<ScheduleSlot> findFullSlotsByPeriod(@Param("period") SchedulePeriod period);
//
//    // Find slots with less than specified registrations
//    @Query("SELECT s FROM ScheduleSlot s WHERE s.period = :period AND SIZE(s.users) < :maxRegistrations")
//    List<ScheduleSlot> findByPeriodAndCurrentRegistrationsLessThan(@Param("period") SchedulePeriod period, @Param("maxRegistrations") Integer maxRegistrations);
//
//    // Tìm slots theo period và session time
//    List<ScheduleSlot> findByPeriodIdAndSessionTime(Long periodId, SessionTimeEnum sessionTime);
//
//    // Tìm slots của user trong cùng ngày và period
//
//
//    // Tìm available slots theo session time
//    @Query("SELECT s FROM ScheduleSlot s WHERE s.period.id = :periodId AND s.sessionTime = :sessionTime AND SIZE(s.users) < s.period.peoplePerSession")
//    List<ScheduleSlot> findAvailableSlotsBySession(@Param("periodId") Long periodId, @Param("sessionTime") SessionTimeEnum sessionTime);
//

//    // check user đã đăng ký phiên này hay chưa
//    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM ScheduleSlot s WHERE s.period = :period AND :user MEMBER OF s.registeredUsers")
//    boolean existsByPeriodAndRegisteredUsersContaining(@Param("period") SchedulePeriod period, @Param("user") User user);
    //boolean existsByPeriodAndUsersContaining(SchedulePeriod period, User user);


}
