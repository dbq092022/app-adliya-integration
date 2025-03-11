package uz.dbq.appadliyaintegration.repository.repo1;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.dbq.appadliyaintegration.entity.entity1.InsurancePolicy;


@Repository
public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicy, String> {

}