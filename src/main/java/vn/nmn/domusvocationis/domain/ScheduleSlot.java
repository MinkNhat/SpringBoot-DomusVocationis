package vn.nmn.domusvocationis.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.nmn.domusvocationis.util.SecurityUtil;
import vn.nmn.domusvocationis.util.constant.SessionTimeEnum;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "schedule_slots")
@Getter
@Setter
public class ScheduleSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant registrationDate;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    private SessionTimeEnum sessionTime;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "slot_user",
            joinColumns = @JoinColumn(name = "slot_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;

    @ManyToOne
    @JoinColumn(name = "period_id")
    private SchedulePeriod period;

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

    public boolean isFull() {
        return this.users.size() >= this.period.getPeoplePerSession();
    }



//    public boolean hasUser(User user) {
//        return this.users.contains(user);
//    }
//
//    public boolean addUser(User user) {
//        if (isFull()) {
//            return false;
//        }
//        return this.users.add(user);
//    }

//    public boolean removeUser(User user) {
//        return this.users.remove(user);
//    }

}
