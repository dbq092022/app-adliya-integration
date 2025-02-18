package uz.dbq.appadliyaintegration.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegistryCourierOrgRequest {
    private String recordId;
    private String registryNumber;
    private String registrationDate;
    private String orgTin;
    private String orgName;
    private String status;
}
