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
@Table(name = "STEP4_APP", schema = "ADLIYA")
public class Step4Application {
    @Id
    @GeneratedValue(generator = "uuid4")
    @GenericGenerator(name = "uuid4", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "VARCHAR(50)")
    private String id;

    private String applicationId;

    private String documentNumber;
    private String policNumber;
    private String policSum;
    private String policDate;
    private String documentDate;
    private String policDeadline;
    private String customsDepozit;
    private String documentFile;
    private String documentType;
    private String policOrgName;
}
