package uz.dbq.appadliyaintegration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.dbq.appadliyaintegration.payload.ApiResponse;
import uz.dbq.appadliyaintegration.service.InsurancePolicyService;

@RestController
@RequestMapping("/api/v1")
public class InsurancePolicyController {
    @Autowired
    InsurancePolicyService insurancePolicyService;

    @GetMapping("/get-insurance-policy")
    public HttpEntity<?> getInsurancePolicy(@RequestParam("tinPin") String tinPin, @RequestParam("type") String type) {
        ApiResponse apiResponse = insurancePolicyService.getInsurancePolicy(tinPin, type);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }
}