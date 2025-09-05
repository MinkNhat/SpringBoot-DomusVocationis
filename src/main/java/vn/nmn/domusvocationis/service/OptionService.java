package vn.nmn.domusvocationis.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.nmn.domusvocationis.domain.Option;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.repository.OptionRepository;
import vn.nmn.domusvocationis.repository.OptionRepository;

@Service
public class OptionService {
    private final OptionRepository optionRepository;

    public OptionService(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    public Option getOptionById(Long id) {
        return this.optionRepository.findById(id).orElse(null);
    }

    public ResPaginationDTO getListOptions(Specification<Option> spec, Pageable pageable) {
        Page<Option> optionPage = this.optionRepository.findAll(spec, pageable);
        ResPaginationDTO rs = new ResPaginationDTO();
        ResPaginationDTO.Meta mt = new ResPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(optionPage.getTotalPages());
        mt.setTotal(optionPage.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(optionPage.getContent());

        return rs;
    }

    public Option create(Option option) {
        return this.optionRepository.save(option);
    }

//    public Option update(Option option, Option dbOption) {
//        if(dbOption != null) {
//            dbOption.setName(cate.getName());
//            dbOption.setDescription(cate.getDescription());
//            dbOption.setActive(cate.isActive());
//            dbOption.setAllowPost(cate.isAllowPost());
//            return this.optionRepository.save(dbOption);
//        }
//
//        return null;
//    }
//
//    public void delete(Long id) {
//        Option cate = this.getOptionById(id);
//        this.postRepository.deleteByOption(cate);
//        this.optionRepository.deleteById(id);
//    }
}
