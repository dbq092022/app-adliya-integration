package uz.dbq.appadliyaintegration.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.dbq.appadliyaintegration.payload.request.ApplicationRequest;
import uz.dbq.appadliyaintegration.payload.request.RegisterRequest;
import uz.dbq.appadliyaintegration.payload.response.ApiResponse;
import uz.dbq.appadliyaintegration.service.InsurancePolicyService;

import java.io.IOException;
import java.text.ParseException;

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

    @PostMapping("/application")
    public HttpEntity<?> getApplication(@Valid @RequestBody ApplicationRequest applicationRequest) throws IOException {
        ApiResponse apiResponse = insurancePolicyService.getApplication(applicationRequest);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @PostMapping("/register")
    public HttpEntity<?> getRegister(@Valid @RequestBody RegisterRequest registerRequest) throws IOException, ParseException {
        ApiResponse apiResponse = insurancePolicyService.getRegister(registerRequest);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

//    @PostMapping("/registry-customs-brokers")
//    public HttpEntity<?> getRegistryCustomsBrokers(@Valid @RequestBody RegistryCustomsBrokersRequest registryCustomsBrokersRequest) {
//        ApiResponse apiResponse = insurancePolicyService.getRegistryCustomsBrokers(registryCustomsBrokersRequest);
//        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
//    }
//
//    @PostMapping("/appeal-customs-brokers")
//    public HttpEntity<?> getAppealCustomsBrokers(@Valid @RequestBody AppealCustomsBrokersRequest appealCustomsBrokersRequest) {
//        ApiResponse apiResponse = insurancePolicyService.getAppealCustomsBrokers(appealCustomsBrokersRequest);
//        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
//    }
//
//    @PostMapping("/registry-courier-org")
//    public HttpEntity<?> getRegistryCourierOrg(@Valid @RequestBody RegistryCourierOrgRequest registryCourierOrgRequest) {
//        ApiResponse apiResponse = insurancePolicyService.getRegistryCourierOrg(registryCourierOrgRequest);
//        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
//    }
//
//    @PostMapping("/appeal-courier-org")
//    public HttpEntity<?> getAppealCourierOrg(@Valid @RequestBody AppealCourierOrgRequest appealCourierOrgRequest) {
//        ApiResponse apiResponse = insurancePolicyService.getAppealCourierOrg(appealCourierOrgRequest);
//        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
//    }
}