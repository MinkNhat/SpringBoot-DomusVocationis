package vn.nmn.domusvocationis.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.nmn.domusvocationis.util.SecurityUtil;
import vn.nmn.domusvocationis.util.constant.SessionTimeEnum;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "sessions")
@Getter
@Setter
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Ngày đăng ký không được để trống")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate registrationDate;
    private Integer totalSlot = 0;
    private String activity;

    @NotNull(message = "Buổi không được để trống")
    @Enumerated(EnumType.STRING)
    private SessionTimeEnum sessionTime;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "session_user",
            joinColumns = @JoinColumn(name = "session_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;

    @ManyToOne
    @JoinColumn(name = "period_id")
    private Period period;

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
