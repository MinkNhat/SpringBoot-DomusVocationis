package vn.nmn.domusvocationis.domain.response.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.nmn.domusvocationis.domain.ScheduleSlot;
import vn.nmn.domusvocationis.util.constant.SessionTimeEnum;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ResSlotByPeriodDTO {
    private Long id;
    private List<Slot> slots;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Slot {
        private long id;
        private Instant registrationDate;
        private SessionTimeEnum sessionTime;
        private List<UserSlot> users;

        @Getter
        @Setter
        @AllArgsConstructor
        public static class UserSlot {
            private long id;
            private String full_name;
        }
    }
}
