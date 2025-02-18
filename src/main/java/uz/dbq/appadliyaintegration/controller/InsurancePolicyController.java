package uz.dbq.appadliyaintegration.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.dbq.appadliyaintegration.payload.ApiResponse;
import uz.dbq.appadliyaintegration.payload.request.AppealCourierOrgRequest;
import uz.dbq.appadliyaintegration.payload.request.AppealCustomsBrokersRequest;
import uz.dbq.appadliyaintegration.payload.request.RegistryCourierOrgRequest;
import uz.dbq.appadliyaintegration.payload.request.RegistryCustomsBrokersRequest;
import uz.dbq.appadliyaintegration.service.InsurancePolicyService;

@RestController
@RequestMapping("/api/v1")
public class InsurancePolicyController {
    @Autowired
    InsurancePolicyService insurancePolicyService;

    @GetMapping("/get-insurance-policy")
    public HttpEntity<?> getInsurancePolicy(@RequestParam(required = false, name = "tinPin") String tinPin,
                                            @RequestParam(required = false, name = "type") String type,
                                            @RequestParam(required = false, name = "policType") String policType) {
        ApiResponse apiResponse = insurancePolicyService.getInsurancePolicy(tinPin, type, policType);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @PostMapping("/registry-customs-brokers")
    public HttpEntity<?> getRegistryCustomsBrokers(@Valid @RequestBody RegistryCustomsBrokersRequest registryCustomsBrokersRequest) {
        ApiResponse apiResponse = insurancePolicyService.getRegistryCustomsBrokers(registryCustomsBrokersRequest);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @PostMapping("/appeal-customs-brokers")
    public HttpEntity<?> getAppealCustomsBrokers(@Valid @RequestBody AppealCustomsBrokersRequest appealCustomsBrokersRequest) {
        ApiResponse apiResponse = insurancePolicyService.getAppealCustomsBrokers(appealCustomsBrokersRequest);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @PostMapping("/registry-courier-org")
    public HttpEntity<?> getRegistryCourierOrg(@Valid @RequestBody RegistryCourierOrgRequest registryCourierOrgRequest) {
        ApiResponse apiResponse = insurancePolicyService.getRegistryCourierOrg(registryCourierOrgRequest);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @PostMapping("/appeal-courier-org")
    public HttpEntity<?> getAppealCourierOrg(@Valid @RequestBody AppealCourierOrgRequest appealCourierOrgRequest) {
        ApiResponse apiResponse = insurancePolicyService.getAppealCourierOrg(appealCourierOrgRequest);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }
}