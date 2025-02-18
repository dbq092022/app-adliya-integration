package uz.dbq.appadliyaintegration.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AppealCustomsBrokersRequest {
    @NotBlank(message = "recordId cannot be empty")
    private String recordId;
    private String registryNumber;
    private String registrationDate;
    private String orgTin;
    private String orgName;
    private String orgAddress;
    private String orgPhoneNumber;
    private String status;
}
