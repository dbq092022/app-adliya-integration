package uz.dbq.appadliyaintegration.entity.entity1;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "APPLICATION", schema = "ADLIYA")
public class Application {

    @Id
    @GeneratedValue(generator = "uuid4")
    @GenericGenerator(name = "uuid4", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "VARCHAR(50)")
    private String id;

    @Column(name = "APPLICATION_ID")
    private String applicationId;

    @Column(name = "GET_DATA")
    private Integer getData;

    @Column(name = "GET_DATA_TIME")
    private Date getDataTime;

    @Column(name = "INSTIME")
    private Date instime;

    @Column(name = "INN")
    private String inn;

    @Column(name = "APPLICATION_CLB")
    private String applicationClb;

}
