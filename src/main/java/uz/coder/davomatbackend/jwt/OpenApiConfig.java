package uz.coder.davomatbackend.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Davomat Backend API")
                        .version("1.0")
                        .description("""
                                ## Davomat App Backend API
                                
                                ### Authentication
                                Most endpoints require JWT authentication. Use the `/auth/login` endpoint to get a token.
                                
                                ### WebSocket Support
                                Real-time notifications are available via WebSocket with JWT authentication:
                                - **Endpoint:** `/ws` (STOMP over SockJS)
                                - **Authentication:** JWT token required in Authorization header
                                - **Test Page:** [WebSocket Test](/websocket-test.html)
                                - **Info API:** [GET /api/websocket/info](/swagger-ui/index.html#/WebSocket%20Info/getWebSocketInfo)
                                
                                **User Queues (automatically routed to authenticated user):**
                                - `/user/queue/students` - Your student notifications
                                - `/user/queue/courses` - Your course notifications  
                                - `/user/queue/groups` - Your group notifications
                                - `/user/queue/attendance` - Your attendance notifications
                                
                                Messages are automatically sent to the correct user based on their JWT token.
                                """)
                        .contact(new Contact()
                                .name("Davomat Team")
                                .email("rahim.mustafo.x@gmail.com")
                        )
                )
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Local Development Server")
                )
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}
