package vn.nmn.domusvocationis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
//import vn.nmn.domusvocationis.config.VNPayConfig;
import vn.nmn.domusvocationis.domain.request.ReqPaymentDTO;
import vn.nmn.domusvocationis.domain.response.payment.ResIpnDTO;
import vn.nmn.domusvocationis.domain.response.payment.ResPaymentDTO;
import vn.nmn.domusvocationis.util.constant.VNPayIpnResponseCode;
import vn.nmn.domusvocationis.util.constant.VNPayParams;
import vn.nmn.domusvocationis.util.error.PaymentException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class VNPayService {
    @Value("${PAY_URL}")
    private String vnp_PayUrl;

    @Value("${RETURN_URL}")
    private String vnp_ReturnUrl;

    @Value("${TMN_CODE}")
    private String vnp_TmnCode ;

    @Value("${SECRET_KEY}")
    private String secretKey;

    @Value("${VERSION}")
    private String vnp_Version;

    @Value("${COMMAND}")
    private String vnp_Command;

    @Value("${ORDER_TYPE}")
    private String orderType;

    private final CryptoService cryptoService;
    private final PaymentService paymentService;

    public VNPayService(CryptoService cryptoService, PaymentService paymentService) {
        this.cryptoService = cryptoService;
        this.paymentService = paymentService;
    }

    public ResPaymentDTO init(ReqPaymentDTO request) throws PaymentException {
        var amount = request.getAmount() * 100L;  // 1. amount * 100
        var txnRef = request.getTxnRef();                       // 2. bookingId
        var returnUrl = buildReturnUrl(txnRef);                 // 3. FE redirect by returnUrl

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
//        var vnCalendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        Calendar vnCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        var createdDate = formatter.format(vnCalendar.getTime());
        System.out.println("VNPAY-time: " + createdDate);
        vnCalendar.add(Calendar.MINUTE, 15);
        var expiredDate = formatter.format(vnCalendar.getTime());    // 4. expiredDate for secure

        var ipAddress = request.getIpAddress();
        var orderInfo = buildPaymentDetail(request);
        var requestId = request.getRequestId();

        Map<String, String> params = new HashMap<>();

        params.put(VNPayParams.VERSION, vnp_Version);
        params.put(VNPayParams.COMMAND, vnp_Command);

        params.put(VNPayParams.TMN_CODE, vnp_TmnCode);
        params.put(VNPayParams.AMOUNT, String.valueOf(amount));
        params.put(VNPayParams.CURRENCY, "VND");

        params.put(VNPayParams.TXN_REF, txnRef);
        params.put(VNPayParams.RETURN_URL, returnUrl);

        params.put(VNPayParams.CREATED_DATE, createdDate);
        params.put(VNPayParams.EXPIRE_DATE, expiredDate);

        params.put(VNPayParams.IP_ADDRESS, ipAddress);
        params.put(VNPayParams.LOCALE, "vn");

        params.put(VNPayParams.ORDER_INFO, orderInfo);
        params.put(VNPayParams.ORDER_TYPE, orderType);

        var initPaymentUrl = buildInitPaymentUrl(params);
        log.debug("[request_id={}] Init payment url: {}", requestId, initPaymentUrl);

        ResPaymentDTO res = new ResPaymentDTO();
        res.setPaymentUrl(initPaymentUrl);
        return res;
    }

    private String buildPaymentDetail(ReqPaymentDTO request) {
        return String.format("Thanh toan don %s", request.getTxnRef());
    }

    private String buildReturnUrl(String txnRef) {
        return String.format(vnp_ReturnUrl, txnRef);
    }

    private String buildInitPaymentUrl(Map<String, String> params) throws PaymentException {
        var hashPayload = new StringBuilder();
        var query = new StringBuilder();
        var fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);   // 1. Sort field names

        var itr = fieldNames.iterator();
        while (itr.hasNext()) {
            var fieldName = itr.next();
            var fieldValue = params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                // 2.1. Build hash data
                hashPayload.append(fieldName);
                hashPayload.append("=");
                hashPayload.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                // 2.2. Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append("=");
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (itr.hasNext()) {
                    query.append("&");
                    hashPayload.append("&");
                }
            }
        }

        // 3. Build secureHash
        var secureHash = cryptoService.sign(hashPayload.toString());

        // 4. Finalize query
        query.append("&vnp_SecureHash=");
        query.append(secureHash);

        return vnp_PayUrl + "?" + query;
    }

    public boolean verifyIpn(Map<String, String> params) throws PaymentException {
        var reqSecureHash = params.get(VNPayParams.SECURE_HASH);
        params.remove(VNPayParams.SECURE_HASH);
        params.remove(VNPayParams.SECURE_HASH_TYPE);
        var hashPayload = new StringBuilder();
        var fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        var itr = fieldNames.iterator();
        while (itr.hasNext()) {
            var fieldName = itr.next();
            var fieldValue = params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                //Build hash data
                hashPayload.append(fieldName);
                hashPayload.append("=");
                hashPayload.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (itr.hasNext()) {
                    hashPayload.append("&");
                }
            }
        }

        var secureHash = cryptoService.sign(hashPayload.toString());
        return secureHash.equals(reqSecureHash);
    }

    public ResIpnDTO process(Map<String, String> params) throws PaymentException {
        if (!this.verifyIpn(params)) {
            return VNPayIpnResponseCode.SIGNATURE_FAILED;
        }

        ResIpnDTO res = new ResIpnDTO();
        var txnRef = params.get(VNPayParams.TXN_REF);
        try {
            var id = Long.parseLong(txnRef);
            this.paymentService.markPayed(id);

            //ghi vao lich su thanh toan

            res = VNPayIpnResponseCode.SUCCESS;
        }
        catch (PaymentException e) {
            res = VNPayIpnResponseCode.UNKNOWN_ERROR;
        }

        log.info("[VNPay Ipn] txnRef: {}, res: {}", txnRef, res);
        return res;
    }
}
