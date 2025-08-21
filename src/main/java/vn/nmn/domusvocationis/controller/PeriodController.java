package vn.nmn.domusvocationis.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.nmn.domusvocationis.domain.Role;
import vn.nmn.domusvocationis.domain.SchedulePeriod;
import vn.nmn.domusvocationis.domain.User;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.service.SchedulePeriodService;
import vn.nmn.domusvocationis.util.annotation.ApiMessage;
import vn.nmn.domusvocationis.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class PeriodController {
    private final SchedulePeriodService periodService;

    public PeriodController(SchedulePeriodService periodService) {
        this.periodService = periodService;
    }

    @GetMapping("/periods/{id}")
    @ApiMessage("get a period")
    public ResponseEntity<SchedulePeriod> getPeriodById(@PathVariable Long id) throws IdInvalidException {
        SchedulePeriod period = periodService.getPeriodById(id);
        if (period == null) {
            throw new IdInvalidException("Phiên đăng ký có id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok(period);
    }

    @GetMapping("/periods")
    @ApiMessage("Fetch periods")
    public ResponseEntity<ResPaginationDTO> getListPeriods(@Filter Specification<SchedulePeriod> spec, Pageable pageable) {
        return ResponseEntity.ok(this.periodService.getListPeriods(spec, pageable));
    }

    @PostMapping("/periods")
    @ApiMessage("Create a period")
    public ResponseEntity<SchedulePeriod> create(@Valid @RequestBody SchedulePeriod period) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.periodService.createPeriod(period));
    }

    @PutMapping("/periods")
    @ApiMessage("Update a period")
    public ResponseEntity<SchedulePeriod> update(@Valid @RequestBody SchedulePeriod period) throws IdInvalidException {
        SchedulePeriod dbPeriod = periodService.getPeriodById(period.getId());
        if (dbPeriod == null) {
            throw new IdInvalidException("Phiên đăng ký có id = " + period.getId() + " không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.periodService.updatePeriod(period, dbPeriod));
    }

    @DeleteMapping("/periods/{id}")
    @ApiMessage("Delete a period")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws IdInvalidException {
        SchedulePeriod period = periodService.getPeriodById(id);
        if (period == null) {
            throw new IdInvalidException("Phiên đăng ký có id = " + id + " không tồn tại");
        }

        this.periodService.deletePeriod(id);
        return ResponseEntity.ok(null);
    }
}
