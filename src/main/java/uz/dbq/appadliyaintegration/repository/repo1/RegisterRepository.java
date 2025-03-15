package uz.dbq.appadliyaintegration.repository.repo1;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import uz.dbq.appadliyaintegration.entity.entity1.Register;

public interface RegisterRepository extends JpaRepository<Register, String>, JpaSpecificationExecutor<Register> {

}