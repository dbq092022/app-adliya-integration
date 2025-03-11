package uz.dbq.appadliyaintegration.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uz.dbq.appadliyaintegration.entity.entity1.InsurancePolicy;
import uz.dbq.appadliyaintegration.payload.ApiResponse;
import uz.dbq.appadliyaintegration.payload.request.AppealCourierOrgRequest;
import uz.dbq.appadliyaintegration.payload.request.AppealCustomsBrokersRequest;
import uz.dbq.appadliyaintegration.payload.request.RegistryCourierOrgRequest;
import uz.dbq.appadliyaintegration.payload.request.RegistryCustomsBrokersRequest;
import uz.dbq.appadliyaintegration.payload.response.InsurancePolicyResponse;
import uz.dbq.appadliyaintegration.repository.repo1.InsurancePolicyRepository;

import java.sql.Timestamp;
import java.util.List;

@Service
public class InsurancePolicyService {
    private final PaymentCheckImpl paymentCheckImpl;
    private final InsurancePolicyRepository insurancePolicyRepository;
    @Qualifier("entityManagerFactoryDataBaseSecond")
    @PersistenceContext(unitName = "dataBaseSecond")
    private EntityManager entityManager;

    public InsurancePolicyService(PaymentCheckImpl paymentCheckImpl, InsurancePolicyRepository insurancePolicyRepository) {
        this.paymentCheckImpl = paymentCheckImpl;
        this.insurancePolicyRepository = insurancePolicyRepository;
    }

    public ApiResponse getInsurancePolicy(String tinPin, String type, String policType) {
        try {
            if (tinPin == null || !tinPin.matches("\\d{9}")) {
                return new ApiResponse("Invalid tinPin: Must be a 9-digit number", false, null);
            }

            if (type == null || !type.matches("[12]")) {
                return new ApiResponse("Invalid type: Must be 1 or 2", false, null);
            } // sugurta - 1, bhm 1000 - 2


            if (type.equals("1")) {
                if (policType == null || !policType.matches("[12]")) {
                    return new ApiResponse("Invalid policType: Must be 1 or 2", false, null);
                } // broker - 1, kurer - 2
                if (policType.equals("1")) {
                    policType = "5";
                } else {
                    policType = "6";
                }

                String sql = "SELECT \n" +
                        "    p.PL_NUM, \n" +
                        "    p.VALID_TO, \n" +
                        "    p.PL_DT, \n" +
                        "    p.PL_SUM, \n" +
                        "    case when p.PL_INTENT_TYPE='5' then 'broker' else 'kuryer' end, \n" +
                        "    a.ORG_NAME \n" +
                        "FROM \n" +
                        "    INSURANCE.AGREEMNT a\n" +
                        "    left join INSURANCE.POLIS p on p.id = a.id\n" +
                        "    where p.PL_INTENT_TYPE in (" + policType + ")  -- 5 broker, 6 kurier\n" +
                        "and a.CL_INN='" + tinPin + "'";

                Query query = entityManager.createNativeQuery(sql);
                List<Object[]> results = query.getResultList();
                InsurancePolicyResponse insurancePolicyResponse = new InsurancePolicyResponse();
                for (Object[] result : results) {
                    insurancePolicyResponse.setPolicNum(result[0] != null ? result[0].toString() : null);
                    insurancePolicyResponse.setPolicData(result[2] != null ? result[2].toString() : null);
                    insurancePolicyResponse.setPolicSum(result[3] != null ? result[3].toString() : null);
                    insurancePolicyResponse.setPolicDeadline(result[1] != null ? result[1].toString() : null);
                    insurancePolicyResponse.setPolicType(result[4] != null ? result[4].toString() : null);
                    insurancePolicyResponse.setPolicOrgName(result[5] != null ? result[5].toString() : null);
                }
                saveInsurancePolicyLog(new InsurancePolicy(tinPin, type, policType, "OK", insurancePolicyResponse.toString(), new Timestamp(System.currentTimeMillis())));
                return new ApiResponse("OK", true, insurancePolicyResponse);
            } else {
                Double result = paymentCheckImpl.result(tinPin);
                if (result >= 1000 * 375000) {
                    saveInsurancePolicyLog(new InsurancePolicy(tinPin, type, policType, "OK", "mablag' yetarli", new Timestamp(System.currentTimeMillis())));
                    return new ApiResponse("OK", true, "mablag' yetarli");
                } else {
                    saveInsurancePolicyLog(new InsurancePolicy(tinPin, type, policType, "OK", "mablag' yetarli emas", new Timestamp(System.currentTimeMillis())));
                    return new ApiResponse("OK", true, "mablag' yetarli emas");
                }
            }
        } catch (Exception e) {
            return new ApiResponse("Error", false, "ko'zda tutilmagan xatolik");
        }
    }

    public ApiResponse getRegistryCustomsBrokers(RegistryCustomsBrokersRequest registryCustomsBrokersRequest) {
        return new ApiResponse("OK", true, null);
    }

    public ApiResponse getAppealCustomsBrokers(AppealCustomsBrokersRequest appealCustomsBrokersRequest) {
        return new ApiResponse("OK", true, null);
    }

    public ApiResponse getRegistryCourierOrg(RegistryCourierOrgRequest registryCourierOrgRequest) {
        return new ApiResponse("OK", true, null);
    }

    public ApiResponse getAppealCourierOrg(AppealCourierOrgRequest appealCourierOrgRequest) {
        return new ApiResponse("OK", true, null);
    }

    public void saveInsurancePolicyLog(InsurancePolicy insurancePolicy) {
        insurancePolicyRepository.save(insurancePolicy);
    }
}
