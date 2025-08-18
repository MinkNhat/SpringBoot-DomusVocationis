package vn.nmn.domusvocationis.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.nmn.domusvocationis.domain.User;
import vn.nmn.domusvocationis.domain.response.user.ResBulkCreateUserDTO;
import vn.nmn.domusvocationis.domain.response.user.ResCreateUserDTO;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.domain.response.user.ResUpdateUserDTO;
import vn.nmn.domusvocationis.domain.response.user.ResUserDTO;
import vn.nmn.domusvocationis.service.UserService;
import vn.nmn.domusvocationis.util.annotation.ApiMessage;
import vn.nmn.domusvocationis.util.error.IdInvalidException;

import java.util.List;


@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    @ApiMessage("Fetch users")
    public ResponseEntity<ResPaginationDTO> getListUsers(@Filter Specification<User> spec, Pageable pageable) {
        return ResponseEntity.ok(this.userService.getListUsers(spec, pageable));
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Fetch a user")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable Long id) throws IdInvalidException {
        User currentUser = this.userService.getUserById(id);
        if(currentUser == null) throw new IdInvalidException("User có ID = " + id + " không tồn tại");

        return ResponseEntity.ok(this.userService.convertToResUserDTO(currentUser));
    }

    @PostMapping("/users")
    @ApiMessage("Create a user")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User reqUser) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(reqUser.getEmail());
        if(isEmailExist) {
            throw new IdInvalidException("Email " + reqUser.getEmail() + " đã tồn tại");
        }

        reqUser.setPassword(this.passwordEncoder.encode(reqUser.getPassword()));
        User newUser = this.userService.create(reqUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(newUser));
    }

    @PostMapping("/users/bulk-create")
    @ApiMessage("Bulk create users")
    public ResponseEntity<ResBulkCreateUserDTO> bulkCreateUser(@RequestBody List<User> reqUsers) throws IdInvalidException {
        if (reqUsers == null || reqUsers.isEmpty()) {
            throw new IdInvalidException("Danh sách user không được để trống");
        }

        for (User user : reqUsers) {
            if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
                user.setPassword(this.passwordEncoder.encode(user.getPassword()));
            }
        }

        ResBulkCreateUserDTO result = this.userService.bulkCreateUsers(reqUsers);

        if (result.getErrorCount() == 0) {
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } else if (result.getSuccessCount() > 0) {
            return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User reqUser) throws IdInvalidException {
        User currentUser = this.userService.update(reqUser);
        if(currentUser == null) throw new IdInvalidException("User có ID = " + reqUser.getId() + " không tồn tại");

        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(currentUser));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) throws IdInvalidException {
        User currentUser = this.userService.getUserById(id);
        if(currentUser == null) throw new IdInvalidException("User có ID = " + id + " không tồn tại");

        this.userService.delete(id);
        return ResponseEntity.ok(null);
    }
}
