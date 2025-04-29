package uz.dbq.appadliyaintegration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uz.dbq.appadliyaintegration.entity.entity1.Application;
import uz.dbq.appadliyaintegration.entity.entity1.InsurancePolicy;
import uz.dbq.appadliyaintegration.entity.entity1.Register;
import uz.dbq.appadliyaintegration.payload.request.*;
import uz.dbq.appadliyaintegration.payload.response.ApiResponse;
import uz.dbq.appadliyaintegration.payload.response.InsurancePolicyResponse;
import uz.dbq.appadliyaintegration.repository.repo1.ApplicationRepository;
import uz.dbq.appadliyaintegration.repository.repo1.InsurancePolicyRepository;
import uz.dbq.appadliyaintegration.repository.repo1.RegisterRepository;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static uz.dbq.appadliyaintegration.config.OkHttpClientConfig.getUnsafeOkHttpClient;

@Service
public class InsurancePolicyService {
    private final PaymentCheckImpl paymentCheckImpl;
    private final InsurancePolicyRepository insurancePolicyRepository;
    private final String mspdIp = "10.190.25.10";
    private final ApplicationRepository applicationRepository;
    private final RegisterRepository registerRepository;


    @Qualifier("entityManagerFactoryDataBaseSecond")
    @PersistenceContext(unitName = "dataBaseSecond")
    private EntityManager entityManager;

    public InsurancePolicyService(PaymentCheckImpl paymentCheckImpl, InsurancePolicyRepository insurancePolicyRepository, ApplicationRepository applicationRepository, RegisterRepository registerRepository) {
        this.paymentCheckImpl = paymentCheckImpl;
        this.insurancePolicyRepository = insurancePolicyRepository;
        this.applicationRepository = applicationRepository;
        this.registerRepository = registerRepository;
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
                        "and a.CL_INN='" + tinPin + "' order by a.VALID_TO desc limit 1";

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

    @Transactional
    public ApiResponse getApplication(ApplicationRequest applicationRequest) throws IOException {
        Application application = new Application();
        application.setApplicationId(applicationRequest.getApplication_id());
        application.setGetData(0);
        application.setInstime(new Timestamp(System.currentTimeMillis()));
        applicationRepository.save(application);

        String token = getToken();
        String url = "https://" + mspdIp + "/v1/application/customs/" + applicationRequest.getApplication_id();

        OkHttpClient client = getUnsafeOkHttpClient(); // Sertifikat tekshirishni o‘chirilgan client

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error: " + response.code() + " - " + response.body().string());
            }

            String responseBody = response.body().string();
            JsonNode rootNode = new ObjectMapper().readTree(responseBody);

            if (rootNode.has("application")) {
                application.setGetDataTime(new Timestamp(System.currentTimeMillis()));
                application.setGetData(1);
                application.setInn(rootNode.path("application").path("tin").asText());
                application.setApplicationClb(responseBody);
                applicationRepository.save(application);

//                ApplicationEntity applicationEntity = objectMapper.readValue(responseBody, ApplicationEntity.class);
//                applicationEntityRepository.save(applicationEntity);
            }
        }

        return new ApiResponse("OK", true, "application has been saved");
    }


    public ApiResponse getRegister(RegisterRequest registerRequest) throws IOException {
        Register register = new Register();
        register.setRegisterId(registerRequest.getRegister_id());
        register.setGetData(0);
        register.setInstime(new Timestamp(System.currentTimeMillis()));
        registerRepository.save(register);

        String token = getToken();
        String url = "https://" + mspdIp + "/v1/register/customs/" + registerRequest.getRegister_id();

        OkHttpClient client = getUnsafeOkHttpClient(); // Sertifikat tekshirishni o‘chirilgan client

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error: " + response.code() + " - " + response.body().string());
            }

            String responseBody = response.body().string();
            JsonNode rootNode = new ObjectMapper().readTree(responseBody);

            if (rootNode.has("register")) {
                JsonNode registerNode = rootNode.path("register");
                register.setActive(registerNode.path("active").asBoolean());
                register.setName(registerNode.path("name").asText());
                register.setTin(registerNode.path("tin").asText());
                register.setPin(registerNode.path("pin").asText());
                register.setRegisterNumber(registerNode.path("registration_number").asText());
                register.setRegisterNumber(registerNode.path("register_number").asText());
                register.setSerial(registerNode.path("serial").asText());
                register.setNumber(registerNode.path("number").asText());

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    Date registrationDate = sdf.parse(registerNode.path("registration_date").asText());
                    register.setRegistrationDate(registrationDate);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                try {
                    Date expiryDate = sdf.parse(registerNode.path("expiry_date").asText());
                    register.setExpiryDate(expiryDate);
                } catch (Exception e) {
                    System.out.println();
                }

                register.setRegionId(registerNode.path("region_id").asText());
                register.setSubRegionId(registerNode.path("sub_region_id").asText());
                register.setAddress(registerNode.path("address").asText());
                register.setCertificateUuid(registerNode.path("certificate_uuid").asText());
                register.setGetDataTime(new Timestamp(System.currentTimeMillis()));
                register.setGetData(1);
//                register.setRegisterClb(responseBody);

                register.setStatus(registerNode.path("status").path("status").asText());
                register.setType(registerNode.path("type").path("oz").asText());
                register.setDocumentId(registerNode.path("document").path("id").asText());
                register.setDocumentName(registerNode.path("document").path("name_oz").asText());
                register.setCategory(registerNode.path("category").path("oz").asText());

                registerRepository.save(register);
            }
        }

        return new ApiResponse("OK", true, "register has been saved");
    }


    public void saveInsurancePolicyLog(InsurancePolicy insurancePolicy) {
        insurancePolicyRepository.save(insurancePolicy);
    }

    public String getToken() throws IOException {
        String url = "https://" + mspdIp + "/v1/oauth/organization/token";

        OkHttpClient client = getUnsafeOkHttpClient(); // Sertifikat tekshirishni o‘chirilgan client

        String jsonBody = new Gson().toJson(Map.of("username", "dbq", "password", "dbq"));

        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error: " + response.code() + " - " + response.body().string());
            }

            String responseBody = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

            return jsonObject.has("access_token") ? jsonObject.get("access_token").getAsString() : null;
        }
    }


}
