package vn.nmn.domusvocationis.domain.response.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.nmn.domusvocationis.util.constant.SessionTimeEnum;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ResSessionByPeriodDTO {
    private Long id;
    private List<Session> sessions;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Session {
        private long id;
        private LocalDate registrationDate;
        private SessionTimeEnum sessionTime;
        private String activity;
        private Integer totalSlot;
        private List<UserSession> users;

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
    }
}
