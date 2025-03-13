package uz.dbq.appadliyaintegration.repository.repo1;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.dbq.appadliyaintegration.entity.entity1.Application;
import uz.dbq.appadliyaintegration.entity.entity1.ApplicationId;


@Repository
public interface ApplicationRepository extends JpaRepository<Application, String> {

}