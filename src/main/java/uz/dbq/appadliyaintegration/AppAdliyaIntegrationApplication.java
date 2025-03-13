package uz.dbq.appadliyaintegration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AppAdliyaIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppAdliyaIntegrationApplication.class, args);
    }

}
