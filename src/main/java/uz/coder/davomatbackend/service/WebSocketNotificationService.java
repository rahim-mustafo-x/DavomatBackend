package uz.coder.davomatbackend.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    // Send to specific authenticated user
    public void notifyUser(String username, String destination, String message) {
        messagingTemplate.convertAndSendToUser(username, destination, message);
    }

    // Notify student added - sends to the teacher/owner
    public void notifyStudentAdded(String username, String message) {
        messagingTemplate.convertAndSendToUser(username, "/queue/students", message);
    }

    // Notify course updated - sends to the teacher/owner
    public void notifyCourseUpdated(String username, String message) {
        messagingTemplate.convertAndSendToUser(username, "/queue/courses", message);
    }

    // Notify group updated - sends to the teacher/owner
    public void notifyGroupUpdated(String username, String message) {
        messagingTemplate.convertAndSendToUser(username, "/queue/groups", message);
    }

    // Notify attendance updated - sends to the specific student
    public void notifyAttendanceUpdated(String username, String message) {
        messagingTemplate.convertAndSendToUser(username, "/queue/attendance", message);
    }

    // Broadcast to all users on a topic (if needed)
    public void broadcastToTopic(String topic, String message) {
        messagingTemplate.convertAndSend("/topic/" + topic, message);
    }
}
