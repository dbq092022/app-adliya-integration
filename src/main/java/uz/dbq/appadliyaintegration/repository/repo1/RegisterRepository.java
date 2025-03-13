package uz.dbq.appadliyaintegration.repository.repo1;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.dbq.appadliyaintegration.entity.entity1.Register;
import uz.dbq.appadliyaintegration.entity.entity1.RegisterId;


@Repository
public interface RegisterRepository extends JpaRepository<Register, String> {

}