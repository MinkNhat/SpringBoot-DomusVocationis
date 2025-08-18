package vn.nmn.domusvocationis.domain.response.user;

import lombok.Getter;
import lombok.Setter;
import vn.nmn.domusvocationis.util.constant.GenderEnum;

import java.time.Instant;

@Getter
@Setter
public class ResUpdateUserDTO {
    private long id;
    private String christianName;
    private String fullName;
    private String email;

    private Instant updatedAt;
    private String updatedBy;
}
