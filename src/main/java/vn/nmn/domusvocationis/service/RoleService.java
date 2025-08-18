package vn.nmn.domusvocationis.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.nmn.domusvocationis.domain.Permission;
import vn.nmn.domusvocationis.domain.Role;
import vn.nmn.domusvocationis.domain.response.ResPaginationDTO;
import vn.nmn.domusvocationis.repository.PermissionRepository;
import vn.nmn.domusvocationis.repository.RoleRepository;

import java.util.List;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean isExistByName(String name) {
        return this.roleRepository.existsByName(name);
    }
    
    public Role getRoleById(Long id) {
        return this.roleRepository.findById(id).orElse(null);
    }

    public ResPaginationDTO getListRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> polePage = this.roleRepository.findAll(spec, pageable);
        ResPaginationDTO rs = new ResPaginationDTO();
        ResPaginationDTO.Meta mt = new ResPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(polePage.getTotalPages());
        mt.setTotal(polePage.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(polePage.getContent());

        return rs;
    }

    public Role create(Role r) {
        if(r.getPermissions() != null) {
            List<Long> reqPermissions = r.getPermissions().stream().map(p -> p.getId()).toList();
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            r.setPermissions(dbPermissions);
        }

        return this.roleRepository.save(r);
    }

    public Role update(Role r) {
        if(r.getPermissions() != null) {
            List<Long> reqPermissions = r.getPermissions().stream().map(p -> p.getId()).toList();
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            r.setPermissions(dbPermissions);
        }

        Role dbRole = this.getRoleById(r.getId());
        if(dbRole != null) {
            dbRole.setName(r.getName());
            dbRole.setDescription(r.getDescription());
            dbRole.setActive(r.isActive());
            dbRole.setPermissions(r.getPermissions());
            return this.roleRepository.save(dbRole);
        }

        return null;
    }

    public void delete(Long id) {
        this.roleRepository.deleteById(id);
    }
}
