package uz.dbq.appadliyaintegration.entity.entity1;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

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

    private Boolean active;
    private String status;
    @Column(name = "type", columnDefinition = "VARCHAR(1000) CCSID 1208")
    private String type;
    @Column(name = "document_id")
    private String documentId;
    @Column(name = "document_name", columnDefinition = "VARCHAR(1000) CCSID 1208")
    private String documentName;
    @Column(name = "category", columnDefinition = "VARCHAR(1000) CCSID 1208")
    private String category;
    @Column(name = "name", columnDefinition = "VARCHAR(1000) CCSID 1208")
    private String name;
    private String tin;
    private String pin;
    @Column(name = "registration_number")
    private String registrationNumber;
    @Column(name = "register_number")
    private String registerNumber;
    private String serial;
    private String number;
    @Column(name = "registration_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yy.MM.dddd")
    private Date registrationDate;
    @Column(name = "expiry_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yy.MM.dddd")
    private Date expiryDate;
    @Column(name = "region_id")
    private String regionId;
    @Column(name = "sub_region_id")
    private String subRegionId;
    @Column(name = "address", columnDefinition = "VARCHAR(1000) CCSID 1208")
    private String address;
    @Column(name = "certificate_uuid")
    private String certificateUuid;
}
