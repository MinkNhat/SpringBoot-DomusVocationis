package vn.nmn.domusvocationis.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.nmn.domusvocationis.util.SecurityUtil;
import vn.nmn.domusvocationis.util.constant.FeeFrequencyEnum;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "FeeTypes")
@Getter
@Setter
public class FeeType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên loại phí không được để trống")
    private String name;
    private String description;
    private boolean active = true;

    @NotNull(message = "Kỳ hạn phí không được để trống")
    private FeeFrequencyEnum frequency;

    @NotNull(message = "Số tiền không được để trống")
    private double amount;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate startDate;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @OneToMany(mappedBy = "feeType", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<FeeRegistration> feeRegistrations;

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
