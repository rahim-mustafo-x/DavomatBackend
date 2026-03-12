package uz.coder.davomatbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DavomatBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DavomatBackendApplication.class, args);
    }

}
