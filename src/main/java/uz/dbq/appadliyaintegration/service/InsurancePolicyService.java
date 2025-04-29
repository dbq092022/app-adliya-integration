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
import uz.dbq.appadliyaintegration.entity.entity1.*;
import uz.dbq.appadliyaintegration.payload.request.ApplicationRequest;
import uz.dbq.appadliyaintegration.payload.request.RegisterRequest;
import uz.dbq.appadliyaintegration.payload.response.ApiResponse;
import uz.dbq.appadliyaintegration.payload.response.InsurancePolicyResponse;
import uz.dbq.appadliyaintegration.repository.repo1.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private final InvoiceRepository invoiceRepository;
    private final Step3AppRepository step3AppRepository;
    private final Step4AppRepository step4AppRepository;
    private final Step1AppRepository step1AppRepository;


    @Qualifier("entityManagerFactoryDataBaseSecond")
    @PersistenceContext(unitName = "dataBaseSecond")
    private EntityManager entityManager;

    public InsurancePolicyService(PaymentCheckImpl paymentCheckImpl, InsurancePolicyRepository insurancePolicyRepository, ApplicationRepository applicationRepository, RegisterRepository registerRepository, InvoiceRepository invoiceRepository, Step3AppRepository step3AppRepository, Step4AppRepository step4AppRepository, Step1AppRepository step1AppRepository) {
        this.paymentCheckImpl = paymentCheckImpl;
        this.insurancePolicyRepository = insurancePolicyRepository;
        this.applicationRepository = applicationRepository;
        this.registerRepository = registerRepository;
        this.invoiceRepository = invoiceRepository;
        this.step3AppRepository = step3AppRepository;
        this.step4AppRepository = step4AppRepository;
        this.step1AppRepository = step1AppRepository;
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

            String applicationId = rootNode.path("application").path("id").asText();
            String documentNameOz = rootNode.path("application").path("document").path("name_oz").asText();
            String categoryNameOz = rootNode.path("application").path("category").path("oz").asText();
            String typeNameOz = rootNode.path("application").path("type").path("oz").asText();
            String applicationType = rootNode.path("application").path("application_type").path("title").path("oz").asText();
            String applicationStatus = rootNode.path("application").path("status").path("title").path("oz").asText();
            String reviewStatus = rootNode.path("application").path("review_status").path("title").path("oz").asText();
            String cabinetType = rootNode.path("application").path("cabinet_type").asText();
            String applicantName = rootNode.path("application").path("applicant_name").asText();
            String tin = rootNode.path("application").path("tin").asText();
            String pin = rootNode.path("application").path("pin").asText();
            String registrationDate = rootNode.path("application").path("registration_date").asText();
            String registrationNumber = rootNode.path("application").path("registration_number").asText();
            String number = rootNode.path("application").path("number").asText();
            String registerId = rootNode.path("application").path("register_id").asText();
            String createdAt = rootNode.path("application").path("created_at").asText();
            String completedAt = rootNode.path("application").path("completed_at").asText();

            if (rootNode.has("application")) {
                application.setApplicationId(applicationId);
                application.setGetDataTime(new Timestamp(System.currentTimeMillis()));
                application.setGetData(1);
                application.setInn(rootNode.path("application").path("tin").asText());
                application.setApplicationClb(responseBody);
                application.setDocumentNameOz(documentNameOz);
                application.setCategoryNameOz(categoryNameOz);
                application.setTypeNameOz(typeNameOz);
                application.setApplicationType(applicationType);
                application.setApplicationStatus(applicationStatus);
                application.setReviewStatus(reviewStatus);
                application.setCabinetType(cabinetType);
                application.setApplicantName(applicantName);
                application.setTin(tin);
                application.setPin(pin);
                application.setRegistrationDate(registrationDate);
                application.setRegistrationNumber(registrationNumber);
                application.setNumber(number);
                application.setRegisterId(registerId);
                application.setCreatedAt(createdAt);
                application.setCompletedAt(completedAt);
                applicationRepository.save(application);
            }

            List<Invoice> invoiceList = new ArrayList<>();

            JsonNode invoices = rootNode.path("application").path("invoices");
            if (invoices.isArray()) {
                for (JsonNode invoiceNode : invoices) {
                    Invoice invoice = new Invoice();
                    invoice.setApplicationId(application.getId());
                    invoice.setSerial(invoiceNode.path("serial").asText());
                    invoice.setStatus(invoiceNode.path("status").asText());
                    invoice.setAmount(invoiceNode.path("amount").asDouble());
                    invoice.setDetail(invoiceNode.path("detail").asText());
                    invoice.setIssueDate(invoiceNode.path("issue_date").asText());
                    invoice.setPayee(invoiceNode.path("payee").asText());
                    invoice.setPayer(invoiceNode.path("payer").asText());
                    invoice.setBankAccount(invoiceNode.path("bank_account").asText());
                    invoice.setBudgetAccount(invoiceNode.path("budget_account").asText());
                    invoice.setBankName(invoiceNode.path("bank_name").asText());
                    invoice.setBankMfo(invoiceNode.path("bank_mfo").asText());
                    invoice.setPaidAt(invoiceNode.path("paid_at").asText());

                    invoiceList.add(invoice);
                }
            }
            invoiceRepository.saveAll(invoiceList);

            JsonNode fieldsStep1 = rootNode.path("application").path("steps").path("1").path("fields");
            if (fieldsStep1 != null) {
                String legalEntityAddress = fieldsStep1.path("LEGAL_ENTITY_ADDRESS").path("value").asText();
                String applicantMobilePhone = fieldsStep1.path("APPLICANT_MOBILE_PHONE").path("value").asText();
                String legalEntityTIN = fieldsStep1.path("LEGAL_ENTITY_TIN").path("value").asText();
                String legalEntitySubRegion = fieldsStep1.path("LEGAL_ENTITY_SUB_REGION").path("value").asText();
                String bankAccount = fieldsStep1.path("BANK_ACCOUNT").path("value").asText();
                String legalEntityName = fieldsStep1.path("LEGAL_ENTITY_NAME").path("value").asText();
                String legalEntityRegion = fieldsStep1.path("LEGAL_ENTITY_REGION").path("value").asText();
                String legalEntityVillage = fieldsStep1.path("LEGAL_ENTITY_VILLAGE").path("value").asText();
                String bankCode = fieldsStep1.path("BANK_CODE").path("value").asText();

                Step1Application step1Application = new Step1Application();
                step1Application.setApplicationId(application.getId());
                step1Application.setLegalEntityAddress(legalEntityAddress);
                step1Application.setApplicantMobilePhone(applicantMobilePhone);
                step1Application.setLegalEntityTIN(legalEntityTIN);
                step1Application.setLegalEntitySubRegion(legalEntitySubRegion);
                step1Application.setBankAccount(bankAccount);
                step1Application.setLegalEntityName(legalEntityName);
                step1Application.setLegalEntityRegion(legalEntityRegion);
                step1Application.setLegalEntityVillage(legalEntityVillage);
                step1Application.setBankCode(bankCode);
                step1AppRepository.save(step1Application);
            }

            JsonNode fieldsStep3 = rootNode.path("application").path("steps").path("3").path("list");

            if (fieldsStep3.isArray()) {
                for (JsonNode personNode : fieldsStep3) {
                    String pinfl = personNode.path("PINFL").path("value").asText();
                    String passport = personNode.path("PASSPORT_SERIAL_NUMBER").path("value").asText();
                    String middleName = personNode.path("MIDDLE_NAME").path("value").asText();
                    String firstName = personNode.path("FIRST_NAME").path("value").asText();
                    String lastName = personNode.path("LAST_NAME").path("value").asText();
                    String positionCompanyName = personNode.path("POSITION_COMPANY_NAME").path("value").asText();
                    String positionDocNumber = personNode.path("POSITION_DOC_NUMBER").path("value").asText();
                    String positionDocDate = personNode.path("POSITION_DOC_DATE").path("value").asText();
                    String selectedSpecialist = personNode.path("SELECTED_SPECIALIST").path("value").asText();
                    String docNumber = personNode.path("DOC_NUMBER").path("value").asText(""); // default ""
                    String docDate = personNode.path("DOC_DATE").path("value").asText("");
                    String docFile = personNode.path("DOC_FILE").path("value").asText("");

                    Step3Application step3Application = new Step3Application();
                    step3Application.setApplicationId(application.getId());
                    step3Application.setPinfl(pinfl);
                    step3Application.setPassport(passport);
                    step3Application.setMiddleName(middleName);
                    step3Application.setFirstName(firstName);
                    step3Application.setLastName(lastName);
                    step3Application.setPositionCompanyName(positionCompanyName);
                    step3Application.setPositionDocNumber(positionDocNumber);
                    step3Application.setPositionDocDate(positionDocDate);
                    step3Application.setSelectedSpecialist(selectedSpecialist);
                    step3Application.setDocNumber(docNumber);
                    step3Application.setDocDate(docDate);
                    step3Application.setDocFile(docFile);
                    step3AppRepository.save(step3Application);
                }
            }

            JsonNode fieldsStep4 = rootNode.path("application").path("steps").path("4").path("fields");

            if (fieldsStep4 != null) {
                String documentNumber = fieldsStep4.path("DOCUMENT_NUMBER").path("value").asText("");
                String policNumber = fieldsStep4.path("POLIC_NUMBER").path("value").asText("");
                String policSum = fieldsStep4.path("POLIC_SUM").path("value").asText("");
                String policDate = fieldsStep4.path("POLIC_DATE").path("value").asText("");
                String documentDate = fieldsStep4.path("DOCUMENT_DATE").path("value").asText("");
                String policDeadline = fieldsStep4.path("POLIC_DEADLINE").path("value").asText("");
                String customsDepozit = fieldsStep4.path("CUSTOMS_DEPOZIT").path("value").asText("");
                String documentFile = fieldsStep4.path("DOCUMENT_FILE").path("value").asText("");
                String documentType = fieldsStep4.path("DOCUMENT_TYPE").path("value").asText("");
                String policOrgName = fieldsStep4.path("POLIC_ORGNAME").path("value").asText("");

                Step4Application step4Application = new Step4Application();
                step4Application.setApplicationId(application.getId());
                step4Application.setDocumentNumber(documentNumber);
                step4Application.setPolicNumber(policNumber);
                step4Application.setPolicSum(policSum);
                step4Application.setPolicDate(policDate);
                step4Application.setDocumentDate(documentDate);
                step4Application.setPolicDeadline(policDeadline);
                step4Application.setCustomsDepozit(customsDepozit);
                step4Application.setDocumentFile(documentFile);
                step4Application.setDocumentType(documentType);
                step4Application.setPolicOrgName(policOrgName);
                step4AppRepository.save(step4Application);
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
