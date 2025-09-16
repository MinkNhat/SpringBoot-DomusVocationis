package vn.nmn.domusvocationis.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.nmn.domusvocationis.domain.Permission;
import vn.nmn.domusvocationis.domain.Role;
import vn.nmn.domusvocationis.domain.User;
import vn.nmn.domusvocationis.repository.PermissionRepository;
import vn.nmn.domusvocationis.repository.RoleRepository;
import vn.nmn.domusvocationis.repository.UserRepository;
import vn.nmn.domusvocationis.util.constant.GenderEnum;

import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseInitializer implements CommandLineRunner {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(PermissionRepository permissionRepository, RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        long countPermissions = this.permissionRepository.count();
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();

        if (countPermissions == 0) {
            ArrayList<Permission> arr = new ArrayList<>();
            arr.add(new Permission("Create a permission", "/api/v1/permissions", "POST", "PERMISSIONS"));
            arr.add(new Permission("Update a permission", "/api/v1/permissions", "PUT", "PERMISSIONS"));
            arr.add(new Permission("Delete a permission", "/api/v1/permissions/{id}", "DELETE", "PERMISSIONS"));
            arr.add(new Permission("Get a permission by id", "/api/v1/permissions/{id}", "GET", "PERMISSIONS"));
            arr.add(new Permission("Get permissions with pagination", "/api/v1/permissions", "GET", "PERMISSIONS"));

            arr.add(new Permission("Create a role", "/api/v1/roles", "POST", "ROLES"));
            arr.add(new Permission("Update a role", "/api/v1/roles", "PUT", "ROLES"));
            arr.add(new Permission("Delete a role", "/api/v1/roles/{id}", "DELETE", "ROLES"));
            arr.add(new Permission("Get a role by id", "/api/v1/roles/{id}", "GET", "ROLES"));
            arr.add(new Permission("Get roles with pagination", "/api/v1/roles", "GET", "ROLES"));

            arr.add(new Permission("Create a user", "/api/v1/users", "POST", "USERS"));
            arr.add(new Permission("Update a user", "/api/v1/users", "PUT", "USERS"));
            arr.add(new Permission("Delete a user", "/api/v1/users/{id}", "DELETE", "USERS"));
            arr.add(new Permission("Get a user by id", "/api/v1/users/{id}", "GET", "USERS"));
            arr.add(new Permission("Get users with pagination", "/api/v1/users", "GET", "USERS"));
            arr.add(new Permission("Create-bulk users", "/api/v1/users/bulk-create", "POST", "USERS"));

            arr.add(new Permission("Create a period", "/api/v1/periods", "POST", "PERIODS"));
            arr.add(new Permission("Update a period", "/api/v1/periods", "PUT", "PERIODS"));
            arr.add(new Permission("Delete a period", "/api/v1/periods/{id}", "DELETE", "PERIODS"));
            arr.add(new Permission("Get a period by id", "/api/v1/periods/{id}", "GET", "PERIODS"));
            arr.add(new Permission("Get periods with pagination", "/api/v1/periods", "GET", "PERIODS"));
            arr.add(new Permission("Get sessions by periods", "/api/v1/periods/{id}/sessions", "GET", "PERIODS"));

            arr.add(new Permission("Create a session", "/api/v1/sessions", "POST", "SESSIONS"));
//            arr.add(new Permission("Update a session", "/api/v1/sessions", "PUT", "SESSIONS"));
            arr.add(new Permission("Get a session by id", "/api/v1/sessions/{id}", "GET", "SESSIONS"));

            arr.add(new Permission("Create a category", "/api/v1/categories", "POST", "CATEGORIES"));
            arr.add(new Permission("Update a category", "/api/v1/categories", "PUT", "CATEGORIES"));
            arr.add(new Permission("Delete a category", "/api/v1/categories/{id}", "DELETE", "CATEGORIES"));
            arr.add(new Permission("Get a category by id", "/api/v1/categories/{id}", "GET", "CATEGORIES"));
            arr.add(new Permission("Get categories with pagination", "/api/v1/categories", "GET", "CATEGORIES"));

            arr.add(new Permission("Create a fee type", "/api/v1/fee-types", "POST", "FEE_TYPES"));
            arr.add(new Permission("Update a fee type", "/api/v1/fee-types", "PUT", "FEE_TYPES"));
            arr.add(new Permission("Delete a fee type", "/api/v1/fee-types/{id}", "DELETE", "FEE_TYPES"));
            arr.add(new Permission("Get a fee type by id", "/api/v1/fee-types/{id}", "GET", "FEE_TYPES"));
            arr.add(new Permission("Get fee types with pagination", "/api/v1/fee-types", "GET", "FEE_TYPES"));

            arr.add(new Permission("Download a file", "/api/v1/files", "POST", "FILES"));
            arr.add(new Permission("Upload a file", "/api/v1/files", "GET", "FILES"));

            this.permissionRepository.saveAll(arr);
        }

        if (countRoles == 0) {
            List<Permission> allPermissions = this.permissionRepository.findAll();

            Role adminRole = new Role();
            adminRole.setName("SUPER_ADMIN");
            adminRole.setDescription("Admin full permissions");
            adminRole.setPermissions(allPermissions);

            this.roleRepository.save(adminRole);
        }

        if (countUsers == 0) {
            User adminUser = new User();
            adminUser.setEmail("admin@gmail.com");
            adminUser.setChristianName(" ");
            adminUser.setPhone("0349171071");
            adminUser.setActive(true);
            adminUser.setFullName("I'm super admin");
            adminUser.setPassword(this.passwordEncoder.encode("123456"));

            Role adminRole = this.roleRepository.findByName("SUPER_ADMIN");
            if (adminRole != null) {
                adminUser.setRole(adminRole);
            }

            this.userRepository.save(adminUser);
        }

        if (countPermissions > 0 && countRoles > 0 && countUsers > 0) {
            System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
        } else
            System.out.println(">>> INITING DATABASE");
    }
}
