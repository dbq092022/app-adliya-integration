package uz.dbq.appadliyaintegration.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uz.dbq.appadliyaintegration.payload.ApiResponse;
import uz.dbq.appadliyaintegration.payload.response.InsurancePolicyResponse;

import java.util.List;

@Service
public class InsurancePolicyService {
    @Qualifier("entityManagerFactoryDataBaseSecond")
    @PersistenceContext(unitName = "dataBaseSecond")
    private EntityManager entityManager;

    public ApiResponse getInsurancePolicy(String tinPin, String type) {
        if (!tinPin.matches("\\d{9}")) {
            return new ApiResponse("Invalid tinPin: Must be a 9-digit number", false, null);
        }

        if (!type.matches("[12]")) {
            return new ApiResponse("Invalid type: Must be 1 or 2", false, null);
        }

        if (type.equals("1")) {
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
                    "    where p.PL_INTENT_TYPE = 5  -- 5 broker, 6 kurier\n" +
                    "and a.CL_INN='" + tinPin + "'";

            Query query = entityManager.createNativeQuery(sql);
            List<Object[]> results = query.getResultList();
            InsurancePolicyResponse insurancePolicyResponse = new InsurancePolicyResponse();
            for (Object[] result : results) {
                insurancePolicyResponse.setPolicNum(result[0] != null ? result[0].toString() : null);
                insurancePolicyResponse.setPolicData(result[1] != null ? result[1].toString() : null);
                insurancePolicyResponse.setPolicSum(result[3] != null ? result[3].toString() : null);
                insurancePolicyResponse.setPolicDeadline(result[2] != null ? result[2].toString() : null);
                insurancePolicyResponse.setPolicType(result[4] != null ? result[4].toString() : null);
                insurancePolicyResponse.setPolicOrgName(result[5] != null ? result[5].toString() : null);
            }
            return new ApiResponse("OK", true, insurancePolicyResponse);
        }

        return new ApiResponse("OK", true, null);
    }
}
