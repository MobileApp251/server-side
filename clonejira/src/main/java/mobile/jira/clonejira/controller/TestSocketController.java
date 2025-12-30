package mobile.jira.clonejira.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // API này dùng để test bắn thông báo cho 1 user cụ thể
    // Gọi bằng Postman HTTP thường: POST http://localhost:8080/test-socket?username=ABC&message=XYZ
    @PostMapping("/test-socket")
    public String trigger(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String username, @RequestParam String message) {

        String uid = userDetails.getUsername();
        System.out.println("Đang gửi tới user: " + uid);

        // Lưu ý: destination KHÔNG CÓ "/user" ở đầu
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/notifications",
                message
        );

        return "Đã gửi lệnh tới " + username;
    }
}