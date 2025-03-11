package uz.dbq.appadliyaintegration.payload.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class InsurancePolicyResponse {
    private String policNum;
    private String policData;
    private String policSum;
    private String policDeadline;
    private String policType;
    private String policOrgName;
}
