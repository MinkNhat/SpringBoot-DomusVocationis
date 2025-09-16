package vn.nmn.domusvocationis.domain.response.fee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.nmn.domusvocationis.domain.FeeType;

import java.time.LocalDate;

@Getter
@Setter
public class ResFeeRegisterDTO {
    private Long id;
    private boolean active;
    private LocalDate registrationDate;
    private LocalDate nextPaymentDate;
    private UserRegister user;
    private FeeType feeType;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserRegister {
        private long id;
        private String full_name;
    }
}
