package vn.nmn.domusvocationis.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.nmn.domusvocationis.util.SecurityUtil;
import vn.nmn.domusvocationis.util.annotation.ChronologicalDates;
import vn.nmn.domusvocationis.util.constant.SchedulePeriodStatusEnum;
import vn.nmn.domusvocationis.util.constant.ScheduleTypeEnum;
import vn.nmn.domusvocationis.util.constant.SessionTimeEnum;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "schedule_periods")
@Getter
@Setter
@ChronologicalDates(
        startField = "registrationStartTime",
        endField = "registrationEndTime",
        message = "Thời gian đăng ký không hợp lệ"
)
@ChronologicalDates(
        startField = "startDate",
        endField = "endDate",
        message = "Thời gian thực hiện không hợp lệ"
)
@ChronologicalDates(
        startField = "registrationStartTime",
        endField = "startDate",
        message = "Thời gian thực hiện phải sau thời gian đăng ký"
)
public class SchedulePeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private SchedulePeriodStatusEnum status;
    private Integer maxSlots;

    @NotNull(message = "Loại phiên không được để trống")
    private ScheduleTypeEnum type;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private Instant startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private Instant endDate;

    @NotNull(message = "Thời điểm bắt đầu đăng ký không được để trống")
    private Instant registrationStartTime;

    @NotNull(message = "Thời điểm kết thúc đăng ký không được để trống")
    private Instant registrationEndTime;

    @Min(value = 1, message = "Số người trong buổi phải lớn hơn 0")
    private Integer peoplePerSession = 1;

    @ElementCollection
    @CollectionTable(name = "period_excluded_days", joinColumns = @JoinColumn(name = "period_id"))
    @Column(name = "day_of_week")
    private Set<Integer> excludedDaysOfWeek;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "period_allowed_sessions", joinColumns = @JoinColumn(name = "period_id"))
    @Column(name = "session_time")
    private Set<SessionTimeEnum> allowedSessions = Set.of(SessionTimeEnum.ALL_DAY);

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @OneToMany(mappedBy = "period", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ScheduleSlot> scheduleSlots;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        this.updatedAt = Instant.now();
    }
}
