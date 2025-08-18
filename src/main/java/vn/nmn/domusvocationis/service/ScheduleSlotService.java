package vn.nmn.domusvocationis.service;

import org.springframework.stereotype.Service;
import vn.nmn.domusvocationis.domain.SchedulePeriod;
import vn.nmn.domusvocationis.domain.ScheduleSlot;
import vn.nmn.domusvocationis.domain.User;
import vn.nmn.domusvocationis.domain.response.schedule.ResSlotDTO;
import vn.nmn.domusvocationis.repository.SchedulePeriodRepository;
import vn.nmn.domusvocationis.repository.ScheduleSlotRepository;
import vn.nmn.domusvocationis.repository.UserRepository;
import vn.nmn.domusvocationis.util.SecurityUtil;
import vn.nmn.domusvocationis.util.constant.SchedulePeriodStatusEnum;
import vn.nmn.domusvocationis.util.error.IdInvalidException;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
public class ScheduleSlotService {
    private final SchedulePeriodRepository periodRepository;
    private final ScheduleSlotRepository slotRepository;
    private final UserRepository userRepository;

    public ScheduleSlotService(SchedulePeriodRepository periodRepository, ScheduleSlotRepository slotRepository, UserRepository userRepository) {
        this.periodRepository = periodRepository;
        this.slotRepository = slotRepository;
        this.userRepository = userRepository;
    }

    public ScheduleSlot getSlotById(Long id) {
        return this.slotRepository.findById(id).orElse(null);
    }

    public ResSlotDTO convertToResSlotDTO(ScheduleSlot slot) {
        ResSlotDTO res = new ResSlotDTO();
        res.setId(slot.getId());
        res.setRegistrationDate(slot.getRegistrationDate());
        res.setSessionTime(slot.getSessionTime());
        res.setCreatedAt(slot.getCreatedAt());
        res.setUpdatedAt(slot.getUpdatedAt());

        res.setPeriod(new ResSlotDTO.PeriodSlot(
                slot.getPeriod().getId(),
                slot.getPeriod().getName(),
                slot.getPeriod().getStatus(),
                slot.getPeriod().getPeoplePerSession(),
                slot.getPeriod().getAllowedSessions()
        ));
        res.setUsers(slot.getUsers().stream().map(u -> new ResSlotDTO.UserSlot(u.getId(), u.getFullName())).toList());

        return res;
    }

    public ResSlotDTO registerSlot(ScheduleSlot slotRequest) throws IdInvalidException {
        ScheduleSlot slot = this.slotRepository.findById(slotRequest.getId()).orElseThrow(() -> new IdInvalidException("Slot không tồn tại"));
        SchedulePeriod period = this.periodRepository.findById(slot.getPeriod().getId()).orElseThrow(() -> new IdInvalidException("Phiên đăng ký không tồn tại"));

        Instant now = Instant.now();
        if (period.getStatus() != SchedulePeriodStatusEnum.OPENING || now.isBefore(period.getRegistrationStartTime()) || now.isAfter(period.getRegistrationEndTime()))
            throw new IllegalStateException("Phiên đăng ký chưa sẵn sàng");

        if (!period.getAllowedSessions().contains(slot.getSessionTime()))
            throw new IllegalStateException("Buổi không được phép đăng ký trong phiên này");

        if (slot.isFull())
            throw new IllegalStateException("Slot đã đầy");

        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User currentUser = this.userRepository.findByEmail(email);

        List<ScheduleSlot> userSlotsInSameDate = slotRepository.findByPeriodAndRegistrationDateAndSessionTimeAndUsersContaining(period, slot.getRegistrationDate(), slot.getSessionTime(), currentUser);
        if (!userSlotsInSameDate.isEmpty()) {
            throw new IllegalStateException(String.format("Bạn đã đăng ký buổi %s trong ngày %s rồi", slot.getSessionTime().getDisplayName().toLowerCase(), slot.getRegistrationDate()));
        }

        slot.getUsers().add(currentUser);

        ScheduleSlot currentSlot = this.slotRepository.save(slot);
        return this.convertToResSlotDTO(currentSlot);
    }

//    public List<ScheduleSlot> getAvailableSlots(Long periodId) {
//        return slotRepository.findAvailableSlots(periodId);
//    }


//    public void unregisterSlot(Long slotId) throws IdInvalidException {
//        String email = SecurityUtil.getCurrentUserLogin().orElse("");
//        User currentUser = this.userRepository.findByEmail(email);
//
//        ScheduleSlot slot = slotRepository.findById(slotId)
//                .orElseThrow(() -> new IdInvalidException("Không tìm thấy slot"));
//
//        // Kiểm tra user có đăng ký slot này không
//        if (!slot.hasUser(currentUser)) {
//            throw new IllegalStateException("Bạn chưa đăng ký slot này.");
//        }
//
//        // Xóa user khỏi slot
//        if (slot.removeUser(currentUser)) {
//            slotRepository.save(slot);
//        } else {
//            throw new IllegalStateException("Không thể hủy đăng ký slot.");
//        }
//    }
//
//    public List<ScheduleSlot> getUserRegisteredSlots(String email) throws IdInvalidException {
//        User user = userRepository.findByEmail(email);
//        if (user == null) {
//            throw new IdInvalidException("User không tồn tại");
//        }
//        return slotRepository.findByUsersContaining(user);
//    }
//
//    public List<ScheduleSlot> getUserRegisteredSlotsInPeriod(Long periodId, String email) throws IdInvalidException {
//        SchedulePeriod period = periodRepository.findById(periodId)
//                .orElseThrow(() -> new IdInvalidException("Period không tồn tại"));
//
//        User user = userRepository.findByEmail(email);
//        if (user == null) {
//            throw new IdInvalidException("User không tồn tại");
//        }
//
//        return slotRepository.findByPeriodAndUsersContaining(period, user);
//    }
//
//    public List<User> getSlotRegisteredUsers(Long slotId) throws IdInvalidException {
//        ScheduleSlot slot = slotRepository.findById(slotId)
//                .orElseThrow(() -> new IdInvalidException("Slot không tồn tại"));
//
//        return slot.getUsers().stream().toList();
//    }

//    public boolean isUserRegisteredToSlot(Long slotId, String email) {
//        User user = userRepository.findByEmail(email);
//        if (user == null) {
//            return false;
//        }
//
//        ScheduleSlot slot = slotRepository.findById(slotId).orElse(null);
//        if (slot == null) {
//            return false;
//        }
//
//        return slot.hasUser(user);
//    }
}
