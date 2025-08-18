package vn.nmn.domusvocationis.domain.response.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.nmn.domusvocationis.domain.SchedulePeriod;
import vn.nmn.domusvocationis.util.constant.SchedulePeriodStatusEnum;
import vn.nmn.domusvocationis.util.constant.SessionTimeEnum;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ResSlotDTO {
    private Long id;
    private Instant registrationDate;
    private SessionTimeEnum sessionTime;
    private PeriodSlot period;

    private Instant createdAt;
    private Instant updatedAt;

    private List<UserSlot> users;

    public int getAvailableSlots() {
        return this.period.getAllowedSessions().size() - this.users.size();
    }

    public int getCurrentRegistrations() {
        return this.users.size();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserSlot {
        private long id;
        private String full_name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class PeriodSlot {
        private long id;
        private String name;
        private SchedulePeriodStatusEnum status;
        private Integer peoplePerSession;
        private Set<SessionTimeEnum> allowedSessions;
    }
}
