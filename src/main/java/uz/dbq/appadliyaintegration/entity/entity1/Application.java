package uz.dbq.appadliyaintegration.entity.entity1;

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

    @Lob
    @Column(name = "APPLICATION_CLB")
    private String applicationClb;

    @Column(name = "DOCUMENT_NAME_OZ")
    private String documentNameOz;

    @Column(name = "CATEGORY_NAME_OZ")
    private String categoryNameOz;

    @Column(name = "TYPE_NAME_OZ")
    private String typeNameOz;

    @Column(name = "APPLICATION_TYPE")
    private String applicationType;

    @Column(name = "APPLICATION_STATUS")
    private String applicationStatus;

    @Column(name = "REVIEW_STATUS")
    private String reviewStatus;

    @Column(name = "CABINET_TYPE")
    private String cabinetType;

    @Column(name = "APPLICANT_NAME")
    private String applicantName;

    @Column(name = "TIN")
    private String tin;

    @Column(name = "PIN")
    private String pin;

    @Column(name = "REGISTRATION_DATE")
    private String registrationDate;

    @Column(name = "REGISTRATION_NUMBER")
    private String registrationNumber;

    @Column(name = "NUMBER")
    private String number;

    @Column(name = "REGISTER_ID")
    private String registerId;

    @Column(name = "CREATED_AT")
    private String createdAt;

    @Column(name = "COMPLETED_AT")
    private String completedAt;
}
