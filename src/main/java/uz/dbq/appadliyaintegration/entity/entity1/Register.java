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
@Table(name = "REGISTER", schema = "ADLIYA")
public class Register {

    @Id
    @GeneratedValue(generator = "uuid4")
    @GenericGenerator(name = "uuid4", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "VARCHAR(50)")
    private String id;

    @Column(name = "GET_DATA")
    private Integer getData;

    @Column(name = "GET_DATA_TIME")
    private Date getDataTime;

    @Column(name = "INSTIME")
    private Date instime;

    @Column(name = "REGISTER_ID")
    private String registerId;

    @Column(name = "REGISTER_CLB")
    private String registerClb;

    @Column(name = "INN")
    private String inn;

}
