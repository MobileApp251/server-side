package mobile.jira.clonejira.controller;

import lombok.RequiredArgsConstructor;
import mobile.jira.clonejira.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping()
    public ResponseEntity<?> getNotifications(
            @AuthenticationPrincipal UserDetails userDetails
    ){
        try {
            String uid = userDetails.getUsername();
            return ResponseEntity.ok(notificationService.getMyNotifications(uid));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
