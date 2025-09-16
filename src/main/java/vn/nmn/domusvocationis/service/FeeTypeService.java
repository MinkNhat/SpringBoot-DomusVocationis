package vn.nmn.domusvocationis.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.nmn.domusvocationis.domain.FeeRegistration;
import vn.nmn.domusvocationis.domain.FeeType;
import vn.nmn.domusvocationis.domain.User;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.repository.FeeTypeRepository;
import vn.nmn.domusvocationis.repository.UserRepository;
import vn.nmn.domusvocationis.util.SecurityUtil;

import java.time.LocalDate;

@Service
public class FeeTypeService {
    private final FeeTypeRepository feeTypeRepository;

    public FeeTypeService(FeeTypeRepository feeTypeRepository) {
        this.feeTypeRepository = feeTypeRepository;
    }

    public FeeType getFeeTypeById(Long id) {
        return this.feeTypeRepository.findById(id).orElse(null);
    }

    public ResPaginationDTO getListFeeTypes(Specification<FeeType> spec, Pageable pageable) {
        Page<FeeType> feeTypePage = this.feeTypeRepository.findAll(spec, pageable);
        ResPaginationDTO rs = new ResPaginationDTO();
        ResPaginationDTO.Meta mt = new ResPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(feeTypePage.getTotalPages());
        mt.setTotal(feeTypePage.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(feeTypePage.getContent());

        return rs;
    }

    public FeeType create(FeeType feeType) {
        return this.feeTypeRepository.save(feeType);
    }

    public FeeType update(FeeType feeType, FeeType dbFeeType) {
        if(dbFeeType != null) {
            dbFeeType.setName(feeType.getName());
            dbFeeType.setDescription(feeType.getDescription());
            dbFeeType.setActive(feeType.isActive());
            dbFeeType.setAmount(feeType.getAmount());
            dbFeeType.setFrequency(feeType.getFrequency());
            dbFeeType.setStartDate(feeType.getStartDate());
            return this.feeTypeRepository.save(dbFeeType);
        }

        return null;
    }

    public void delete(Long id) {
//        FeeType feeType = this.getFeeTypeById(id);
//        this.postRepository.deleteByFeeType(feeType);
        this.feeTypeRepository.deleteById(id);
    }
}
