package uz.dbq.appadliyaintegration.entity.entity1;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "STEP3_APP", schema = "ADLIYA")
public class Step3Application {
    @Id
    @GeneratedValue(generator = "uuid4")
    @GenericGenerator(name = "uuid4", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "VARCHAR(50)")
    private String id;

    private String applicationId;
    private String pinfl;
    private String passport;
    private String middleName;
    private String firstName;
    private String lastName;
    private String positionCompanyName;
    private String positionDocNumber;
    private String positionDocDate;
    private String selectedSpecialist;
    private String docNumber;
    private String docDate;
    private String docFile;
}
