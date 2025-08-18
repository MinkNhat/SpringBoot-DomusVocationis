package vn.nmn.domusvocationis.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.nmn.domusvocationis.domain.Permission;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.service.PermissionService;
import vn.nmn.domusvocationis.util.annotation.ApiMessage;
import vn.nmn.domusvocationis.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

//    @GetMapping("/jobs/{id}")
//    @ApiMessage("Fetch a job")
//    public ResponseEntity<Job> getJobById(@PathVariable Long id) throws IdInvalidException {
//        Job currentJob = this.jobService.getJobById(id);
//        if(currentJob == null) throw new IdInvalidException("Job có ID = " + id + " không tồn tại");
//
//        return ResponseEntity.ok(this.jobService.getJobById(id));
//    }
//
    @GetMapping("/permissions")
    @ApiMessage("Fetch permissions")
    public ResponseEntity<ResPaginationDTO> getListJobs(@Filter Specification<Permission> spec, Pageable pageable) {
        return ResponseEntity.ok(this.permissionService.getListPermissions(spec, pageable));
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a permissions")
    public ResponseEntity<Permission> create(@Valid @RequestBody Permission reqPermission) throws IdInvalidException {
        if(this.permissionService.isPermissionExist(reqPermission))
            throw new IdInvalidException("Permission đã tồn tại");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.create(reqPermission));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> update(@Valid @RequestBody Permission reqPermission) throws IdInvalidException {
        Permission currentPermission = this.permissionService.getPermissionById(reqPermission.getId());
        if(currentPermission == null)
            throw new IdInvalidException("Permission có ID = " + reqPermission.getId() + " không tồn tại");

        if(this.permissionService.isPermissionExist(reqPermission))
            if(this.permissionService.isExistByName(reqPermission.getName()))
                throw new IdInvalidException("Permission đã tồn tại");

        return ResponseEntity.ok(this.permissionService.update(reqPermission));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a permission")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws IdInvalidException {
        Permission currentPermission = this.permissionService.getPermissionById(id);
        if(currentPermission == null)
            throw new IdInvalidException("Permission có ID = " + id + " không tồn tại");

        this.permissionService.delete(id);
        return ResponseEntity.ok(null);
    }
}
