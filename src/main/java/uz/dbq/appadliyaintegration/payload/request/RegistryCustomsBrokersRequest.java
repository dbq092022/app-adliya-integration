package uz.dbq.appadliyaintegration.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegistryCustomsBrokersRequest {
    @NotNull(message = "recordId cannot be empty")
    private String recordId;
    @NotNull(message = "registryNumber cannot be empty")
    private String registryNumber;
    @NotNull(message = "registrationDate cannot be empty")
    private String registrationDate;
    private String orgTin;
    private String orgName;
    private String status;
}
