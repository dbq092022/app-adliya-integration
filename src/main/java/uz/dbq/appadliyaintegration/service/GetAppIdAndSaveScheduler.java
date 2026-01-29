package uz.dbq.appadliyaintegration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uz.dbq.appadliyaintegration.payload.request.ApplicationRequest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class GetAppIdAndSaveScheduler {

    private final InsurancePolicyService insurancePolicyService;
    private static final int PAGE_SIZE = 50;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public GetAppIdAndSaveScheduler(InsurancePolicyService insurancePolicyService) {
        this.insurancePolicyService = insurancePolicyService;
    }

//    @Scheduled(cron = "0 1 0 * * *", zone = "Asia/Tashkent")
    @Scheduled(cron = "0 */10 * * * *", zone = "Asia/Tashkent")
    public void saveAppAndRegister() throws IOException {
        String token = "Bearer" + insurancePolicyService.getToken();
//        LocalDate startDate = LocalDate.of(2025, 10, 31);
//        LocalDate endDate = LocalDate.of(2025, 11, 3);
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        String startStr = startDate.format(formatter);
        String endStr = endDate.format(formatter);

        fetchAllIds(token, startStr, endStr);
    }


    public void fetchAllIds(String token, String dateBegin, String dateEnd) throws IOException {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        for (int page = 0; ; page++) {
            HttpUrl url = new HttpUrl.Builder()
                    .scheme("https")
                    .host("api.licenses.uz")
                    .addPathSegments("v1/application/external/customs/daily")
                    .addQueryParameter("application_date_begin", dateBegin)
                    .addQueryParameter("application_date_end", dateEnd)
                    .addQueryParameter("page", String.valueOf(page))
                    .addQueryParameter("size", String.valueOf(PAGE_SIZE))
                    .addQueryParameter("sort", "id,desc")
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Authorization", token)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("HTTP " + response.code());
                }

                JsonNode root = mapper.readTree(response.body().string());
                JsonNode data = root.path("data");
                if (!data.isArray() || data.isEmpty()) break;

                for (JsonNode item : data) {
                    String id = item.path("id").asText();
                    insurancePolicyService.getApplication(new ApplicationRequest(id));
                }

                if (data.size() < PAGE_SIZE) break;
            }
        }
    }
}
