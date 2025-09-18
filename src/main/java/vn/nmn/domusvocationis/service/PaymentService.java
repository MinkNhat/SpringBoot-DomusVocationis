package vn.nmn.domusvocationis.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.nmn.domusvocationis.domain.Payment;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.domain.response.fee.ResFeeRegisterDTO;
import vn.nmn.domusvocationis.repository.PaymentRepository;
import vn.nmn.domusvocationis.util.constant.PaymentMethodEnum;
import vn.nmn.domusvocationis.util.constant.PaymentStatusEnum;
import vn.nmn.domusvocationis.util.error.PaymentException;

import java.time.Instant;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public ResPaginationDTO getListPaymentsByUser(Specification<Payment> spec, Pageable pageable, Long userId) {
        Specification<Payment> byUserId = null;
        if (userId != null) {
            byUserId = (root, query, cb) -> {
                query.distinct(true);
                return cb.equal(root.get("user").get("id"), userId);
            };
        }

        Specification<Payment> finalSpec = Specification.where(byUserId).and(spec);
        Page<Payment> page = this.paymentRepository.findAll(finalSpec, pageable);

        ResPaginationDTO rs = new ResPaginationDTO();
        ResPaginationDTO.Meta mt = new ResPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(page.getContent());

        return rs;
    }

    public ResPaginationDTO getListPayments(Specification<Payment> spec, Pageable pageable) {
        Page<Payment> page = this.paymentRepository.findAll(spec, pageable);

        ResPaginationDTO rs = new ResPaginationDTO();
        ResPaginationDTO.Meta mt = new ResPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(page.getContent());

        return rs;
    }

    public void markPayed(Long id) throws PaymentException {
        Payment pay = paymentRepository.findById(id).orElse(null);
        if(pay == null) throw new PaymentException("Payment id = " + id + " không tồn tại");

        pay.setPaymentDate(Instant.now());
        pay.setStatus(PaymentStatusEnum.COMPLETED);
        pay.setMethod(PaymentMethodEnum.VNPAY);

        this.paymentRepository.save(pay);
    }
}
