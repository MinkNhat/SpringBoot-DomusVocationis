package vn.nmn.domusvocationis.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.nmn.domusvocationis.domain.Role;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.service.RoleService;
import vn.nmn.domusvocationis.util.annotation.ApiMessage;
import vn.nmn.domusvocationis.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

        @GetMapping("/roles/{id}")
    @ApiMessage("Fetch a role")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) throws IdInvalidException {
            Role currentRole = this.roleService.getRoleById(id);
            if(currentRole == null)
                throw new IdInvalidException("Vai trò có ID = " + id + " không tồn tại");

        return ResponseEntity.ok(this.roleService.getRoleById(id));
    }

    @GetMapping("/roles")
    @ApiMessage("Fetch roles")
    public ResponseEntity<ResPaginationDTO> getListJobs(@Filter Specification<Role> spec, Pageable pageable) {
        return ResponseEntity.ok(this.roleService.getListRoles(spec, pageable));
    }

    @PostMapping("/roles")
    @ApiMessage("Create a roles")
    public ResponseEntity<Role> create(@Valid @RequestBody Role reqRole) throws IdInvalidException {
        if(this.roleService.isExistByName(reqRole.getName()))
            throw new IdInvalidException("Vai trò " + reqRole.getName() + " đã tồn tại");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(reqRole));
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> update(@Valid @RequestBody Role reqRole) throws IdInvalidException {
        Role currentRole = this.roleService.getRoleById(reqRole.getId());
        if(currentRole == null)
            throw new IdInvalidException("Vai trò có ID = " + reqRole.getId() + " không tồn tại");

//        if(this.roleService.isExistByName(reqRole.getName()))
//            throw new IdInvalidException("Vai trò " + reqRole.getName() + " đã tồn tại");

        return ResponseEntity.ok(this.roleService.update(reqRole));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws IdInvalidException {
        Role currentRole = this.roleService.getRoleById(id);
        if(currentRole == null)
            throw new IdInvalidException("Vai trò có ID = " + id + " không tồn tại");

        this.roleService.delete(id);
        return ResponseEntity.ok(null);
    }
}
