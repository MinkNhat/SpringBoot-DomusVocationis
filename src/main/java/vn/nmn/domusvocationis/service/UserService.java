package vn.nmn.domusvocationis.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nmn.domusvocationis.domain.Permission;
import vn.nmn.domusvocationis.domain.Role;
import vn.nmn.domusvocationis.domain.User;
import vn.nmn.domusvocationis.domain.response.user.ResBulkCreateUserDTO;
import vn.nmn.domusvocationis.domain.response.user.ResCreateUserDTO;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.domain.response.user.ResUpdateUserDTO;
import vn.nmn.domusvocationis.domain.response.user.ResUserDTO;
import vn.nmn.domusvocationis.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    @Transactional(readOnly = true)
    public boolean checkUserPermission(String email, String apiPath, String method) {
        User user = getUserByUsername(email);
        if(user == null || user.getRole() == null) {
            return false;
        }

        List<Permission> permissions = user.getRole().getPermissions();
        return permissions.stream().anyMatch(
                item -> item.getApiPath().equals(apiPath) && item.getMethod().equals(method)
        );
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.getUserByUsername(email);
        if(currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public User getUserById(Long id) {
        return this.userRepository.findById(id).orElse(null);
    }

    public User getUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    public ResPaginationDTO getListUsers(Specification<User> spec, Pageable pageable) {
        Page<User> userPage = this.userRepository.findAll(spec, pageable);
        ResPaginationDTO rs = new ResPaginationDTO();
        ResPaginationDTO.Meta mt = new ResPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(userPage.getTotalPages());
        mt.setTotal(userPage.getTotalElements());

        List<ResUserDTO> listUsers = userPage.getContent().stream().map(item -> this.convertToResUserDTO(item)).toList();

        rs.setMeta(mt);
        rs.setResult(listUsers);

        return rs;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();

        res.setId(user.getId());
        res.setChristianName(user.getChristianName());
        res.setFullName(user.getFullName());
        res.setEmail(user.getEmail());
        res.setPhone(user.getPhone());
        res.setGender(user.getGender());
        res.setBirth(user.getBirth());
        res.setAvatar(user.getAvatar());
        res.setAddress(user.getAddress());
        res.setActive(user.isActive());
        res.setTeam(user.getTeam());

        res.setFatherName(user.getFatherName());
        res.setFatherPhone(user.getFatherPhone());
        res.setMotherName(user.getMotherName());
        res.setMotherPhone(user.getMotherPhone());

        res.setParish(user.getParish());
        res.setDeanery(user.getDeanery());
        res.setSpiritualDirectorName(user.getSpiritualDirectorName());
        res.setSponsoringPriestName(user.getSponsoringPriestName());

        res.setUniversity(user.getUniversity());
        res.setMajor(user.getMajor());

        res.setCreatedAt(user.getCreatedAt());
        res.setUpdatedAt(user.getUpdatedAt());

        if(user.getRole() != null) {
            ResUserDTO.RoleUser role = new ResUserDTO.RoleUser(user.getRole().getId(), user.getRole().getName());
            res.setRole(role);
        }

        return res;
    }

    public User create(User user) {
        if(user.getRole() != null) {
            Role r = this.roleService.getRoleById(user.getRole().getId());
            user.setRole(r);
        }

        return this.userRepository.save(user);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();

        res.setId(user.getId());
        res.setChristianName(user.getChristianName());
        res.setFullName(user.getFullName());
        res.setEmail(user.getEmail());

        res.setCreatedAt(user.getCreatedAt());
        res.setCreatedBy(user.getCreatedBy());

        return res;
    }

    @Transactional
    public ResBulkCreateUserDTO bulkCreateUsers(List<User> users) {
        int successCount = 0;
        int errorCount = 0;

        ResBulkCreateUserDTO response = new ResBulkCreateUserDTO(successCount, errorCount, new ArrayList<>());

        // Tạo validator
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            try {
                // Validate bean trước
                Set<ConstraintViolation<User>> violations = validator.validate(user);
                if (!violations.isEmpty()) {
                    errorCount++;
                    String errorMessage = violations.iterator().next().getMessage();
                    response.getErrorDetails().add(new ResBulkCreateUserDTO.ErrorItem(i, errorMessage));
                    continue;
                }

                if (isEmailExist(user.getEmail())) {
                    errorCount++;
                    response.getErrorDetails().add(new ResBulkCreateUserDTO.ErrorItem(i, "Email " + user.getEmail() + " đã tồn tại"));
                    continue;
                }

                // Tạo user
                this.create(user);
                successCount++;

            } catch (Exception e) {
                errorCount++;
                response.getErrorDetails().add(new ResBulkCreateUserDTO.ErrorItem(i, e.getMessage()));
            }
        }

        response.setSuccessCount(successCount);
        response.setErrorCount(errorCount);

        return response;
    }

    public User update(User user) {
        User currentUser = this.getUserById(user.getId());
        if(currentUser != null) {
            currentUser.setChristianName(user.getChristianName());
            currentUser.setFullName(user.getFullName());
            currentUser.setPhone(user.getPhone());
            currentUser.setGender(user.getGender());
            currentUser.setBirth(user.getBirth());
            currentUser.setAvatar(user.getAvatar());
            currentUser.setAddress(user.getAddress());
            currentUser.setActive(user.isActive());
            currentUser.setTeam(user.getTeam());

            currentUser.setFatherName(user.getFatherName());
            currentUser.setFatherPhone(user.getFatherPhone());
            currentUser.setMotherName(user.getMotherName());
            currentUser.setMotherPhone(user.getMotherPhone());

            currentUser.setParish(user.getParish());
            currentUser.setDeanery(user.getDeanery());
            currentUser.setSpiritualDirectorName(user.getSpiritualDirectorName());
            currentUser.setSponsoringPriestName(user.getSponsoringPriestName());

            currentUser.setUniversity(user.getUniversity());
            currentUser.setMajor(user.getMajor());

            if(user.getRole() != null) {
                Role r = this.roleService.getRoleById(user.getRole().getId());
                currentUser.setRole(r);
            } else {
                currentUser.setRole(null);
            }

            return this.userRepository.save(currentUser);
        }

        return null;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();

        res.setId(user.getId());
        res.setChristianName(user.getChristianName());
        res.setFullName(user.getFullName());
        res.setEmail(user.getEmail());

        res.setUpdatedAt(user.getUpdatedAt());
        res.setUpdatedBy(user.getUpdatedBy());

        return res;
    }

    public void delete(Long id) {
        this.userRepository.deleteById(id);
    }


}
