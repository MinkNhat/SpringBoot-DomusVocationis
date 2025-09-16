package vn.nmn.domusvocationis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.nmn.domusvocationis.domain.FeeRegistration;

@Repository
public interface FeeRegistrationRepository extends JpaRepository<FeeRegistration, Long>, JpaSpecificationExecutor<FeeRegistration> {
}
