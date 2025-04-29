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
@Table(name = "STEP1_APP", schema = "ADLIYA")
public class Step1Application {
    @Id
    @GeneratedValue(generator = "uuid4")
    @GenericGenerator(name = "uuid4", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "VARCHAR(50)")
    private String id;
    private String applicationId;
    private String legalEntityAddress;
    private String applicantMobilePhone;
    private String legalEntityTIN;
    private String legalEntitySubRegion;
    private String bankAccount;
    private String legalEntityName;
    private String legalEntityRegion;
    private String legalEntityVillage;
    private String bankCode;
}
