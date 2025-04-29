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
@Table(name = "INVOICE", schema = "ADLIYA")
public class Invoice {
    @Id
    @GeneratedValue(generator = "uuid4")
    @GenericGenerator(name = "uuid4", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "VARCHAR(50)")
    private String id;

    private String applicationId;

    private String serial;
    private String status;
    private double amount;
    private String detail;
    private String issueDate;
    private String payee;
    private String payer;
    private String bankAccount;
    private String budgetAccount;
    private String bankName;
    private String bankMfo;
    private String paidAt;
}
