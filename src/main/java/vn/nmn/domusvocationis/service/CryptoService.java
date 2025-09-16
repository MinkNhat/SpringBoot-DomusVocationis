package vn.nmn.domusvocationis.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.nmn.domusvocationis.util.error.PaymentException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class CryptoService {

    private final Mac mac = Mac.getInstance("HmacSHA512");

    @Value("${SECRET_KEY}")
    private String secretKey;

    public CryptoService() throws NoSuchAlgorithmException {
    }

    @PostConstruct
    void init() throws InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA512");
        mac.init(secretKeySpec);
    }


    public String sign(String data) throws PaymentException {
        try {
            return toHexString(mac.doFinal(data.getBytes()));
        }
        catch (Exception e) {
            throw new PaymentException("Có lỗi xảy ra khi thanh toán");
        }
    }

    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
