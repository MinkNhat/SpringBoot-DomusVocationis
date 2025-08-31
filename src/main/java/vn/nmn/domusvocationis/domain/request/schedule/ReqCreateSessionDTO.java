package vn.nmn.domusvocationis.domain.request.schedule;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import vn.nmn.domusvocationis.util.constant.SessionTimeEnum;

import java.time.Instant;

@Getter
@Setter
public class ReqCreateSessionDTO {
    private Long id;

    private Instant registrationDate;
    private Integer totalSlot;
    private String activity;

    @Enumerated(EnumType.STRING)
    private SessionTimeEnum sessionTime;

}
