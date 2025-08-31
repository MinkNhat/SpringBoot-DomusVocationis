package vn.nmn.domusvocationis.domain.response.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.nmn.domusvocationis.util.constant.PeriodStatusEnum;
import vn.nmn.domusvocationis.util.constant.SessionTimeEnum;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ResSessionDTO {
    private Long id;
    private LocalDate registrationDate;
    private SessionTimeEnum sessionTime;
    private Integer totalSlot;
    private String activity;
    private PeriodSession period;

    private Instant createdAt;
    private Instant updatedAt;

    private List<UserSession> users;

    public int getAvailableSlots() {
        return this.totalSlot - this.users.size();
    }

    public int getCurrentRegistrations() {
        return this.users.size();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserSession {
        private long id;
        private String full_name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class PeriodSession {
        private long id;
        private String name;
        private PeriodStatusEnum status;
    }
}
