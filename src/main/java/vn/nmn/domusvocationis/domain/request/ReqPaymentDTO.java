package vn.nmn.domusvocationis.domain.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReqPaymentDTO {
    private String requestId;
    private String ipAddress;
    private long userId;
    private String txnRef;
    private long amount;
}
