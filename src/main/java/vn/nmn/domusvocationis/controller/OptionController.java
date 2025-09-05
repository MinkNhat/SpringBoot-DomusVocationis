package vn.nmn.domusvocationis.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.nmn.domusvocationis.domain.Option;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.service.OptionService;
import vn.nmn.domusvocationis.util.annotation.ApiMessage;
import vn.nmn.domusvocationis.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class OptionController {
    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }
    //
//    @GetMapping("/options/{id}")
//    @ApiMessage("get a option")
//    public ResponseEntity<Option> getOptionById(@PathVariable Long id) throws IdInvalidException {
//        Option option = optionService.getOptionById(id);
//        if (option == null) {
//            throw new IdInvalidException("Option có id = " + id + " không tồn tại");
//        }
//        return ResponseEntity.ok(option);
//    }
//
//    @GetMapping("/options")
//    @ApiMessage("Fetch options")
//    public ResponseEntity<ResPaginationDTO> getListCategories(@Filter Specification<Option> spec, Pageable pageable) {
//        return ResponseEntity.ok(this.optionService.getListCategories(spec, pageable));
//    }
//
    @PostMapping("/options")
    @ApiMessage("Create a option")
    public ResponseEntity<Option> create(@Valid @RequestBody Option option) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.optionService.create(option));
    }

//    @PutMapping("/options")
//    @ApiMessage("Update a option")
//    public ResponseEntity<Option> update(@Valid @RequestBody Option option) throws IdInvalidException {
//        Option dbOption = optionService.getOptionById(option.getId());
//        if (dbOption == null) {
//            throw new IdInvalidException("Option có id = " + option.getId() + " không tồn tại");
//        }
//        return ResponseEntity.status(HttpStatus.OK).body(this.optionService.update(option, dbOption));
//    }
//
//    @DeleteMapping("/options/{id}")
//    @ApiMessage("Delete a option")
//    public ResponseEntity<Void> delete(@PathVariable Long id) throws IdInvalidException {
//        Option option = optionService.getOptionById(id);
//        if (option == null) {
//            throw new IdInvalidException("Option có id = " + id + " không tồn tại");
//        }
//
//        this.optionService.delete(id);
//        return ResponseEntity.ok(null);
//    }
}
