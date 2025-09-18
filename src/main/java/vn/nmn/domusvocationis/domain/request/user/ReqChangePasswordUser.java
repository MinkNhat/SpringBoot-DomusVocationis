package vn.nmn.domusvocationis.domain.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqChangePasswordUser {
    @NotBlank(message = "Mật khẩu cũ không được để trống")
    private String oldPassword;
    @NotBlank(message = "Mật khẩu mới không được để trống")
    private String newPassword;
}
