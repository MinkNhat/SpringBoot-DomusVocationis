package vn.nmn.domusvocationis.util.constant;

import vn.nmn.domusvocationis.domain.response.payment.ResIpnDTO;

public class VNPayIpnResponseCode {
    public static final ResIpnDTO SUCCESS = new ResIpnDTO("00", "Successful");
    public static final ResIpnDTO SIGNATURE_FAILED = new ResIpnDTO("97", "Signature failed");
    public static final ResIpnDTO ORDER_NOT_FOUND = new ResIpnDTO("01", "Order not found");
    public static final ResIpnDTO UNKNOWN_ERROR = new ResIpnDTO("99", "Unknown error");
}
