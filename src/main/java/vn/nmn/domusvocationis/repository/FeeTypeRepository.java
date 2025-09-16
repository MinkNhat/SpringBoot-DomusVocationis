package vn.nmn.domusvocationis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.nmn.domusvocationis.domain.FeeType;

@Repository
public interface FeeTypeRepository extends JpaRepository<FeeType, Long>, JpaSpecificationExecutor<FeeType> {
}
