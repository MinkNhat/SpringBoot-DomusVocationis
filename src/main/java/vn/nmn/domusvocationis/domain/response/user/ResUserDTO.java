package vn.nmn.domusvocationis.domain.response.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.nmn.domusvocationis.util.constant.GenderEnum;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResUserDTO {
    private long id;
    private String christianName;
    private String fullName;
    private String email;
    private String phone;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate birth;
    private GenderEnum gender;
    private String avatar;
    private String address;

    private boolean active;
    private Integer team;

    private String fatherName;
    private String fatherPhone;
    private String motherName;
    private String motherPhone;

    private String parish;
    private String deanery;

    private String spiritualDirectorName;
    private String sponsoringPriestName;

    private String university;
    private String major;

    private Instant createdAt;
    private Instant updatedAt;

    private RoleUser role;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleUser {
        private long id;
        private String name;
    }
}
