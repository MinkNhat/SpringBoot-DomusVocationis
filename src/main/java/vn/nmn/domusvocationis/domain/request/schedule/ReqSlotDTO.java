package vn.nmn.domusvocationis.domain.request.schedule;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import vn.nmn.domusvocationis.domain.SchedulePeriod;
import vn.nmn.domusvocationis.util.constant.SessionTimeEnum;

import java.time.Instant;

@Getter
@Setter
public class ReqSlotDTO {
    private Long id;

    private Instant registrationDate;
    private SchedulePeriod period;

    @Enumerated(EnumType.STRING)
    private SessionTimeEnum sessionTime;
}
