package uz.dbq.appadliyaintegration.repository.repo1;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import uz.dbq.appadliyaintegration.entity.entity1.Application;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, String>, JpaSpecificationExecutor<Application> {
    Optional<Application> findByApplicationId(String applicationId);
}