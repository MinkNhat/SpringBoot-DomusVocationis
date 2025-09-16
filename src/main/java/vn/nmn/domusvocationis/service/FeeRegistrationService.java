package vn.nmn.domusvocationis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.nmn.domusvocationis.domain.*;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.domain.response.fee.ResFeeRegisterDTO;
import vn.nmn.domusvocationis.domain.response.post.ResPostDTO;
import vn.nmn.domusvocationis.repository.FeeRegistrationRepository;
import vn.nmn.domusvocationis.repository.PaymentRepository;
import vn.nmn.domusvocationis.repository.UserRepository;
import vn.nmn.domusvocationis.util.SecurityUtil;
import vn.nmn.domusvocationis.util.constant.PaymentStatusEnum;
import vn.nmn.domusvocationis.util.error.IdInvalidException;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class FeeRegistrationService {
    private final FeeRegistrationRepository feeRegistrationRepository;
    private final FeeTypeService feeTypeService;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    public FeeRegistrationService(FeeRegistrationRepository feeRegistrationRepository, FeeTypeService feeTypeService, UserRepository userRepository, PaymentRepository paymentRepository) {
        this.feeRegistrationRepository = feeRegistrationRepository;
        this.feeTypeService = feeTypeService;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    public ResFeeRegisterDTO convertToResFeeRegisterDTO(FeeRegistration regis) {
        ResFeeRegisterDTO res = new ResFeeRegisterDTO();

        res.setId(regis.getId());
        res.setActive(regis.isActive());
        res.setRegistrationDate(regis.getRegistrationDate());
        res.setNextPaymentDate(regis.getNextPaymentDate());
        res.setFeeType(regis.getFeeType());

        if(regis.getUser() != null) {
            ResFeeRegisterDTO.UserRegister user = new ResFeeRegisterDTO.UserRegister(regis.getUser().getId(), regis.getUser().getFullName());
            res.setUser(user);
        }

        return res;
    }

    public FeeRegistration getFeeRegistrationById(Long id) {
        return this.feeRegistrationRepository.findById(id).orElse(null);
    }

    public ResPaginationDTO getListFeeRegistrations(Specification<FeeRegistration> spec, Pageable pageable, Long userId) {
        Specification<FeeRegistration> activeFee = (root, query, cb) -> {
            var feeTypeJoin = root.join("feeType");
            return cb.equal(feeTypeJoin.get("active"), true);
        };

        Specification<FeeRegistration> byUserId = null;
        if (userId != null) {
            byUserId = (root, query, cb) -> {
                query.distinct(true);
                return cb.equal(root.get("user").get("id"), userId);
            };
        }

        Specification<FeeRegistration> finalSpec = Specification.where(activeFee).and(byUserId).and(spec);
        Page<FeeRegistration> page = this.feeRegistrationRepository.findAll(finalSpec, pageable);

        ResPaginationDTO rs = new ResPaginationDTO();
        ResPaginationDTO.Meta mt = new ResPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());

        rs.setMeta(mt);

        List<ResFeeRegisterDTO> listResult = page.stream().map(item -> convertToResFeeRegisterDTO(item)).toList();
        rs.setResult(listResult);

        return rs;
    }

    public FeeRegistration create(FeeRegistration feeRegis) throws IdInvalidException {
        FeeType feeType = this.feeTypeService.getFeeTypeById(feeRegis.getFeeType().getId());
        if(feeType == null) {
            throw new IdInvalidException("Loại phí có id = " + feeRegis.getFeeType().getId() + " không tồn tại");
        }
        feeRegis.setFeeType(feeType);

        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User currentUser = this.userRepository.findByEmail(email);
        feeRegis.setUser(currentUser);

        switch (feeType.getFrequency()) {
            case WEEKLY -> feeRegis.setNextPaymentDate(feeType.getStartDate().plusWeeks(1));
            case MONTHLY -> feeRegis.setNextPaymentDate(feeType.getStartDate().plusMonths(1));
            case YEARLY -> feeRegis.setNextPaymentDate(feeType.getStartDate().plusYears(1));
            default -> feeRegis.setNextPaymentDate(null);
        }

        FeeRegistration registered = this.feeRegistrationRepository.save(feeRegis);

        //create payment
        Payment payment = new Payment();
        payment.setAmount(feeType.getAmount());
        payment.setStatus(PaymentStatusEnum.PENDING);
        payment.setUser(currentUser);
        payment.setFeeRegistration(registered);
        this.paymentRepository.save(payment);


        return registered;
    }

    public FeeRegistration update(FeeRegistration feeRegis, FeeRegistration dbFeeRegis) {
        if(dbFeeRegis != null) {
            dbFeeRegis.setActive(feeRegis.isActive());

            dbFeeRegis.getPayments().forEach(p -> {
                if(p.getStatus() == PaymentStatusEnum.PENDING) {
                    p.setActive(false);
                }
            });

            return this.feeRegistrationRepository.save(dbFeeRegis);
        }

        return null;
    }


//    public void delete(Long id) {
////        FeeType feeType = this.getFeeTypeById(id);
////        this.postRepository.deleteByFeeType(feeType);
//        this.feeRegistrationRepository.deleteById(id);
//    }

}
