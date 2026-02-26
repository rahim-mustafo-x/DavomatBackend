package uz.coder.davomatbackend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/websocket")
@Tag(name = "WebSocket Info", description = "WebSocket connection information and available topics")
public class WebSocketInfoController {

    @GetMapping("/info")
    @Operation(
            summary = "Get WebSocket connection information",
            description = """
                    WebSocket endpoint and available queues for real-time notifications.
                    
                    **Connection URL:** ws://localhost:8080/ws (or wss:// for production)
                    
                    **Authentication:** Required - Pass JWT token in Authorization header during STOMP connection
                    
                    **Available User Queues (automatically routed to authenticated user):**
                    - /user/queue/students - Student notifications for your account
                    - /user/queue/courses - Course notifications for your account
                    - /user/queue/groups - Group notifications for your account
                    - /user/queue/attendance - Attendance notifications for your account
                    
                    **How to connect:**
                    1. Use SockJS client library
                    2. Connect to /ws endpoint with JWT token in headers
                    3. Subscribe to /user/queue/* destinations
                    4. Messages are automatically routed based on your JWT token
                    
                    **Example (JavaScript):**
                    ```javascript
                    const socket = new SockJS('http://localhost:8080/ws');
                    const stompClient = Stomp.over(socket);
                    
                    const token = 'your-jwt-token';
                    stompClient.connect(
                        {'Authorization': 'Bearer ' + token},
                        function(frame) {
                            // Subscribe to your personal queue
                            stompClient.subscribe('/user/queue/students', function(message) {
                                console.log('Received:', message.body);
                            });
                        }
                    );
                    ```
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "WebSocket information retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "endpoint": "/ws",
                                                        "protocol": "STOMP over SockJS",
                                                        "authentication": "JWT Bearer Token required in Authorization header",
                                                        "userQueues": {
                                                            "students": "/user/queue/students",
                                                            "courses": "/user/queue/courses",
                                                            "groups": "/user/queue/groups",
                                                            "attendance": "/user/queue/attendance"
                                                        },
                                                        "testPage": "/websocket-test.html"
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    public Map<String, Object> getWebSocketInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("endpoint", "/ws");
        info.put("protocol", "STOMP over SockJS");
        info.put("authentication", "JWT Bearer Token required in Authorization header");
        
        Map<String, String> queues = new HashMap<>();
        queues.put("students", "/user/queue/students");
        queues.put("courses", "/user/queue/courses");
        queues.put("groups", "/user/queue/groups");
        queues.put("attendance", "/user/queue/attendance");
        info.put("userQueues", queues);
        
        info.put("testPage", "/websocket-test.html");
        
        return info;
    }
}
