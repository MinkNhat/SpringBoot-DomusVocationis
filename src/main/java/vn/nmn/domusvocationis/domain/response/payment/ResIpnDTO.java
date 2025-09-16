package vn.nmn.domusvocationis.domain.response.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResIpnDTO {
    private String responseCode;
    private String message;
}
