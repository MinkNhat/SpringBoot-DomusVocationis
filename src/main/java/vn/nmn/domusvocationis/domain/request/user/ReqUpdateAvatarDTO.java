package vn.nmn.domusvocationis.domain.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateAvatarDTO {
    @NotBlank(message = "Tên file không được để trống")
    private String fileName;
}
