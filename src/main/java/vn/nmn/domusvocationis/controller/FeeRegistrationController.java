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
import vn.nmn.domusvocationis.domain.User;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.domain.response.fee.ResFeeRegisterDTO;
import vn.nmn.domusvocationis.service.FeeRegistrationService;
import vn.nmn.domusvocationis.service.FeeTypeService;
import vn.nmn.domusvocationis.service.UserService;
import vn.nmn.domusvocationis.util.annotation.ApiMessage;
import vn.nmn.domusvocationis.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class FeeRegistrationController {
    private final FeeRegistrationService feeRegistrationService;
    private final UserService userService;

    public FeeRegistrationController(FeeRegistrationService feeRegistrationService, UserService userService) {
        this.feeRegistrationService = feeRegistrationService;
        this.userService = userService;
    }

    @GetMapping("/fee-registers/{id}")
    @ApiMessage("get a fee register")
    public ResponseEntity<ResFeeRegisterDTO> getFeeRegistrationById(@PathVariable Long id) throws IdInvalidException {
        FeeRegistration feeRegistration = feeRegistrationService.getFeeRegistrationById(id);
        if (feeRegistration == null) {
            throw new IdInvalidException("Đăng ký có id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok(this.feeRegistrationService.convertToResFeeRegisterDTO(feeRegistration));
    }

    @GetMapping("/users/{id}/fee-registers")
    @ApiMessage("Fetch fee register by user")
    public ResponseEntity<ResPaginationDTO> getListFeeTypesByUserId(@PathVariable Long id, @Filter Specification<FeeRegistration> spec, Pageable pageable) throws IdInvalidException {
        User user = userService.getUserById(id);
        if(user == null) {
            throw new IdInvalidException("User có id = " + id + " không tồn tại");
        }

        return ResponseEntity.ok(this.feeRegistrationService.getListFeeRegistrations(spec, pageable, user.getId()));
    }

    @PostMapping("/fee-registers")
    @ApiMessage("Create a fee register")
    public ResponseEntity<ResFeeRegisterDTO> create(@Valid @RequestBody FeeRegistration feeRegistration) throws IdInvalidException {
        FeeRegistration regis = this.feeRegistrationService.create(feeRegistration);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.feeRegistrationService.convertToResFeeRegisterDTO(regis));
    }

    @PutMapping("/fee-registers")
    @ApiMessage("Update a fee register")
    public ResponseEntity<ResFeeRegisterDTO> update(@RequestBody FeeRegistration feeRegistration) throws IdInvalidException {
        FeeRegistration dbFeeRegis = feeRegistrationService.getFeeRegistrationById(feeRegistration.getId());
        if (dbFeeRegis == null) {
            throw new IdInvalidException("Đăng ký có id = " + feeRegistration.getId() + " không tồn tại");
        }

        FeeRegistration updatedRegis = this.feeRegistrationService.update(feeRegistration, dbFeeRegis);
        return ResponseEntity.status(HttpStatus.OK).body(this.feeRegistrationService.convertToResFeeRegisterDTO(updatedRegis));
    }

//    @DeleteMapping("/fee-register/{id}")
//    @ApiMessage("Delete a feeType")
//    public ResponseEntity<Void> delete(@PathVariable Long id) throws IdInvalidException {
//        FeeType feeType = feeTypeService.getFeeTypeById(id);
//        if (feeType == null) {
//            throw new IdInvalidException("Loại phí có id = " + id + " không tồn tại");
//        }
//
//        this.feeTypeService.delete(id);
//        return ResponseEntity.ok(null);
//    }
}
