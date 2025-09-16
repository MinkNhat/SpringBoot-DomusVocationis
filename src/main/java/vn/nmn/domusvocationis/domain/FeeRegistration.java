package vn.nmn.domusvocationis.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.nmn.domusvocationis.util.SecurityUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name = "FeeRegistrations")
@Getter
@Setter
public class FeeRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean active = true;

    @NotNull(message = "Ngày đăng ký không được để trống")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate registrationDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate nextPaymentDate;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "fee_type_id")
    @NotNull(message = "Loại phí không được để trống")
    private FeeType feeType;

    @OneToMany(mappedBy = "feeRegistration", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Payment> payments;

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
