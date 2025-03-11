package uz.dbq.appadliyaintegration.entity.entity1;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "INSURANCE_POLICY", schema = "ADLIYA")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InsurancePolicy {

    @Id
    @GeneratedValue(generator = "uuid4")
    @GenericGenerator(name = "uuid4", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "VARCHAR(50)")
    private String id;

    @Column(name = "TINPIN")
    private String tinpin;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "POLIC_TYPE")
    private String policType;

    @Column(name = "MESSAGE")
    private String message;

    @Column(name = "RESP_JSON")
    private String respJson;

    @Column(name = "INSTIME")
    private Date instime;

    public InsurancePolicy(String tinpin, String type, String policType, String message, String respJson, Date instime) {
        this.tinpin = tinpin;
        this.type = type;
        this.policType = policType;
        this.message = message;
        this.respJson = respJson;
        this.instime = instime;
    }
}
