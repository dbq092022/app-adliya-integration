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
    public HttpEntity<?> getRegister(@Valid @RequestBody RegisterRequest registerRequest) throws IOException {
        ApiResponse apiResponse = insurancePolicyService.getRegister(registerRequest);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }
}