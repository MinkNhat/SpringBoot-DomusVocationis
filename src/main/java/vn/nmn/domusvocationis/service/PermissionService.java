package vn.nmn.domusvocationis.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.nmn.domusvocationis.domain.Permission;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.repository.PermissionRepository;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission p) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(p.getModule(), p.getApiPath(), p.getMethod());
    }

    public boolean isExistByName(String name) {
        return this.permissionRepository.existsByName(name);
    }

    public Permission getPermissionById(Long id) {
        return this.permissionRepository.findById(id).orElse(null);
    }

    public ResPaginationDTO getListPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> permissionPage = this.permissionRepository.findAll(spec, pageable);
        ResPaginationDTO rs = new ResPaginationDTO();
        ResPaginationDTO.Meta mt = new ResPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(permissionPage.getTotalPages());
        mt.setTotal(permissionPage.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(permissionPage.getContent());

        return rs;
    }

    public Permission create(Permission p) {
        return this.permissionRepository.save(p);
    }

    public Permission update(Permission p) {
        Permission dbPermission = this.getPermissionById(p.getId());
        if(dbPermission != null) {
            dbPermission.setName(p.getName());
            dbPermission.setModule(p.getModule());
            dbPermission.setApiPath(p.getApiPath());
            dbPermission.setMethod(p.getMethod());

            return this.permissionRepository.save(dbPermission);
        }

        return null;
    }

    public void delete(Long id) {
        Permission currentPermission = this.getPermissionById(id);
        currentPermission.getRoles().forEach(role -> role.getPermissions().remove(currentPermission));

        this.permissionRepository.delete(currentPermission);
    }
}
