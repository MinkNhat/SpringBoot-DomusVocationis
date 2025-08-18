package vn.nmn.domusvocationis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.nmn.domusvocationis.domain.Role;
import vn.nmn.domusvocationis.domain.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    long countByActiveAndRole(boolean active, Role role);
    User findByEmail(String email);
    User findByRefreshTokenAndEmail(String token, String email);
    boolean existsByEmail(String email);
}
