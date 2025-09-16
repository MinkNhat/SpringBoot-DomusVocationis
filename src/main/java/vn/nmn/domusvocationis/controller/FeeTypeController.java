package vn.nmn.domusvocationis.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.nmn.domusvocationis.domain.FeeRegistration;
import vn.nmn.domusvocationis.domain.FeeType;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.service.FeeTypeService;
import vn.nmn.domusvocationis.util.annotation.ApiMessage;
import vn.nmn.domusvocationis.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class FeeTypeController {
    private final FeeTypeService feeTypeService;

    public FeeTypeController(FeeTypeService feeTypeService) {
        this.feeTypeService = feeTypeService;
    }

    @GetMapping("/fee-types/{id}")
    @ApiMessage("get a fee type")
    public ResponseEntity<FeeType> getFeeTypeById(@PathVariable Long id) throws IdInvalidException {
        FeeType feeType = feeTypeService.getFeeTypeById(id);
        if (feeType == null) {
            throw new IdInvalidException("Loại phí có id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok(feeType);
    }

    @GetMapping("/fee-types")
    @ApiMessage("Fetch fee types")
    public ResponseEntity<ResPaginationDTO> getListFeeTypes(@Filter Specification<FeeType> spec, Pageable pageable) {
        return ResponseEntity.ok(this.feeTypeService.getListFeeTypes(spec, pageable));
    }

    @PostMapping("/fee-types")
    @ApiMessage("Create a fee type")
    public ResponseEntity<FeeType> create(@Valid @RequestBody FeeType feeType) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.feeTypeService.create(feeType));
    }

    @PutMapping("/fee-types")
    @ApiMessage("Update a feeType")
    public ResponseEntity<FeeType> update(@Valid @RequestBody FeeType feeType) throws IdInvalidException {
        FeeType dbFeeType = feeTypeService.getFeeTypeById(feeType.getId());
        if (dbFeeType == null) {
            throw new IdInvalidException("Loại phí có id = " + feeType.getId() + " không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.feeTypeService.update(feeType, dbFeeType));
    }

    @DeleteMapping("/fee-types/{id}")
    @ApiMessage("Delete a feeType")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws IdInvalidException {
        FeeType feeType = feeTypeService.getFeeTypeById(id);
        if (feeType == null) {
            throw new IdInvalidException("Loại phí có id = " + id + " không tồn tại");
        }

        this.feeTypeService.delete(id);
        return ResponseEntity.ok(null);
    }
}
