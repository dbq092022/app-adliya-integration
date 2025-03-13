package uz.dbq.appadliyaintegration.entity.entity1;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

@Entity
@Table(name = "REGISTER_ID", schema = "ADLIYA")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterId {
    @Id
    @GeneratedValue(generator = "uuid4")
    @GenericGenerator(name = "uuid4", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "VARCHAR(50)")
    private String id;

    private String registerId;

    private Integer getData;

    @Column(name = "GET_DATA_TIME")
    private Date getDataTime;

    @Column(name = "INSTIME")
    private Date instime;
}
