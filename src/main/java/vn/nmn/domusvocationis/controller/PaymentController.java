package vn.nmn.domusvocationis.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.nmn.domusvocationis.domain.FeeRegistration;
import vn.nmn.domusvocationis.domain.Payment;
import vn.nmn.domusvocationis.domain.User;
import vn.nmn.domusvocationis.domain.request.ReqPaymentDTO;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.domain.response.payment.ResIpnDTO;
import vn.nmn.domusvocationis.domain.response.payment.ResPaymentDTO;
import vn.nmn.domusvocationis.service.PaymentService;
import vn.nmn.domusvocationis.service.UserService;
import vn.nmn.domusvocationis.service.VNPayService;
import vn.nmn.domusvocationis.util.VNPayUtil;
import vn.nmn.domusvocationis.util.annotation.ApiMessage;
import vn.nmn.domusvocationis.util.error.IdInvalidException;
import vn.nmn.domusvocationis.util.error.PaymentException;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class PaymentController {
    private final VNPayService VNPayService;
    private final UserService userService;
    private final PaymentService paymentService;

    public PaymentController(VNPayService VNPayService, UserService userService, PaymentService paymentService) {
        this.VNPayService = VNPayService;
        this.userService = userService;
        this.paymentService = paymentService;
    }

    @PostMapping("/payments/vn-pay")
    public ResponseEntity<ResPaymentDTO> pay(@RequestBody ReqPaymentDTO request, HttpServletRequest httpServletRequest) throws PaymentException {
        String ipAddress = VNPayUtil.getIpAddress(httpServletRequest);
        request.setIpAddress(ipAddress);
        log.info("[VNPay payment] Params: {}", "Toi da o day");

        return ResponseEntity.ok(VNPayService.init(request));
    }

    @GetMapping("payments/vn-pay-ipn")
    public ResponseEntity<ResIpnDTO> processIpn(@RequestParam Map<String, String> params) throws PaymentException {
        log.info("[VNPay Ipn] Params: {}", params);
        return ResponseEntity.ok(VNPayService.process(params));
    }

    @GetMapping("/users/{id}/payments")
    @ApiMessage("Fetch payments by user")
    public ResponseEntity<ResPaginationDTO> getListFeeTypesByUserId(@PathVariable Long id, @Filter Specification<Payment> spec, Pageable pageable) throws IdInvalidException {
        User user = userService.getUserById(id);
        if(user == null) {
            throw new IdInvalidException("User có id = " + id + " không tồn tại");
        }

        return ResponseEntity.ok(this.paymentService.getListPayments(spec, pageable, user.getId()));
    }
}
