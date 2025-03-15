package uz.dbq.appadliyaintegration.repository.repo1;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import uz.dbq.appadliyaintegration.entity.entity1.Application;

public interface ApplicationRepository extends JpaRepository<Application, String>, JpaSpecificationExecutor<Application> {

}