package uz.dbq.appadliyaintegration.repository.repo1;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import uz.dbq.appadliyaintegration.entity.entity1.Invoice;
import uz.dbq.appadliyaintegration.entity.entity1.Step3Application;
import uz.dbq.appadliyaintegration.entity.entity1.Step4Application;

public interface Step3AppRepository extends JpaRepository<Step3Application, String>, JpaSpecificationExecutor<Step3Application> {

}