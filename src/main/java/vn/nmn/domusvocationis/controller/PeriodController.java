package vn.nmn.domusvocationis.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.nmn.domusvocationis.domain.Period;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.domain.response.schedule.ResSessionByPeriodDTO;
import vn.nmn.domusvocationis.service.PeriodService;
import vn.nmn.domusvocationis.util.annotation.ApiMessage;
import vn.nmn.domusvocationis.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class PeriodController {
    private final PeriodService periodService;

    public PeriodController(PeriodService periodService) {
        this.periodService = periodService;
    }

    @GetMapping("/periods/{id}/sessions")
    @ApiMessage("get sessions by period")
    public ResponseEntity<ResSessionByPeriodDTO> getSessionsByPeriod(@PathVariable Long id) throws IdInvalidException {
        Period period = periodService.getPeriodById(id);
        if (period == null) {
            throw new IdInvalidException("Phiên đăng ký có id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok(this.periodService.getSessionByPeriod(period));
    }

    @GetMapping("/periods/{id}")
    @ApiMessage("get a period")
    public ResponseEntity<Period> getPeriodById(@PathVariable Long id) throws IdInvalidException {
        Period period = periodService.getPeriodById(id);
        if (period == null) {
            throw new IdInvalidException("Phiên đăng ký có id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok(period);
    }

    @GetMapping("/periods")
    @ApiMessage("Fetch periods")
    public ResponseEntity<ResPaginationDTO> getListPeriods(@Filter Specification<Period> spec, Pageable pageable) {
        return ResponseEntity.ok(this.periodService.getListPeriods(spec, pageable));
    }

    @GetMapping("/open-periods")
    @ApiMessage("Fetch open periods")
    public ResponseEntity<ResPaginationDTO> getOpenPeriods(Pageable pageable) {
        return ResponseEntity.ok(this.periodService.getOpenPeriods(pageable));
    }

    @PostMapping("/periods")
    @ApiMessage("Create a period")
    public ResponseEntity<Period> create(@Valid @RequestBody Period period) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.periodService.createPeriod(period));
    }

    @PutMapping("/periods")
    @ApiMessage("Update a period")
    public ResponseEntity<Period> update(@Valid @RequestBody Period period) throws IdInvalidException {
        Period dbPeriod = periodService.getPeriodById(period.getId());
        if (dbPeriod == null) {
            throw new IdInvalidException("Phiên đăng ký có id = " + period.getId() + " không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.periodService.updatePeriod(period, dbPeriod));
    }

    @DeleteMapping("/periods/{id}")
    @ApiMessage("Delete a period")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws IdInvalidException {
        Period period = periodService.getPeriodById(id);
        if (period == null) {
            throw new IdInvalidException("Phiên đăng ký có id = " + id + " không tồn tại");
        }

        this.periodService.deletePeriod(id);
        return ResponseEntity.ok(null);
    }
}
