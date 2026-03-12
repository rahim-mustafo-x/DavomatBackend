package uz.coder.davomatbackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${HOST_IP:192.168.1.150}")
    private String hostIp;

    @Value("${APP_PORT:8080}")
    private String appPort;

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl("http://" + hostIp + ":" + appPort);
        server.setDescription("Davomat Server");

        return new OpenAPI()
                .servers(List.of(server));
    }
}