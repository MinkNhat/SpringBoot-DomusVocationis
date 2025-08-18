package vn.nmn.domusvocationis.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.nmn.domusvocationis.domain.SchedulePeriod;
import vn.nmn.domusvocationis.domain.ScheduleSlot;
import vn.nmn.domusvocationis.domain.response.schedule.ResSlotDTO;
import vn.nmn.domusvocationis.service.ScheduleSlotService;
import vn.nmn.domusvocationis.util.annotation.ApiMessage;
import vn.nmn.domusvocationis.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SlotController {
    private final ScheduleSlotService slotService;

    public SlotController(ScheduleSlotService slotService) {
        this.slotService = slotService;
    }

    @GetMapping("/slots/{id}")
    @ApiMessage("get a slot")
    public ResponseEntity<ResSlotDTO> getSlotById(@PathVariable Long id) throws IdInvalidException {
        ScheduleSlot slot = slotService.getSlotById(id);
        if (slot == null) {
            throw new IdInvalidException("Slot có id = " + id + " không tồn tại");
        }

        return ResponseEntity.ok(slotService.convertToResSlotDTO(slot));
    }

    @PutMapping("/slots")
    @ApiMessage("Regis a slot")
    public ResponseEntity<ResSlotDTO> registrationSlot(@Valid @RequestBody ScheduleSlot slot) throws IdInvalidException {
        return ResponseEntity.ok(this.slotService.registerSlot(slot));
    }
}
